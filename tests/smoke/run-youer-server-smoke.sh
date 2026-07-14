#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERVER_DIR="${SERVER_DIR:-$ROOT_DIR/server-smoke}"
TIMEOUT_SECONDS="${TIMEOUT_SECONDS:-300}"
READY_TIMEOUT_SECONDS="${READY_TIMEOUT_SECONDS:-180}"
JAVA_HOME="${JAVA21_HOME:-$HOME/.gradle/jdks/eclipse_adoptium-21-amd64-linux.2}"
JAVA_BIN="$JAVA_HOME/bin/java"
SERVER_JAR_SOURCE="${SERVER_JAR_SOURCE:-/home/aurlemon/code/Joban-Client-Mod/youer-1.21.1-cb6ddeab-server.jar}"
SERVER_JAR_NAME="server.jar"
LOG_FILE="${LOG_FILE:-$SERVER_DIR/logs/latest.log}"
FIFO_PATH="$SERVER_DIR/.server-stdin"
MOD_JAR="${MOD_JAR:-$(find "$ROOT_DIR/neoforge/build/libs" -maxdepth 1 -type f -name 'neoforge-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)}"
MTR_JAR_SOURCE="${MTR_JAR_SOURCE:-$HOME/.gradle/caches/modules-2/files-2.1/maven.modrinth/minecraft-transit-railway/NEOFORGE-4.1.0-beta.2+1.21.1/3c25af1f04b5a9178bafa505ffc2fd265525e60d/minecraft-transit-railway-NEOFORGE-4.1.0-beta.2+1.21.1.jar}"
UNIVERSE_DIR="${UNIVERSE_DIR:-$SERVER_DIR/worlds/run-$$}"
LEVEL_NAME="${LEVEL_NAME:-run-$$}"
DEFAULT_SERVER_PORT="$((25586 + (BASHPID % 1000)))"
SERVER_PORT="${SERVER_PORT:-}"

cleanup() {
  if [[ -n "${SERVER_PID:-}" ]] && kill -0 "$SERVER_PID" >/dev/null 2>&1; then
    printf 'stop\n' >&${FIFO_FD} || true
    wait "$SERVER_PID" || true
  fi
  if [[ -n "${FIFO_FD:-}" ]]; then
    exec {FIFO_FD}>&- || true
  fi
  rm -f "$FIFO_PATH"
}

trap cleanup EXIT INT TERM

require_file() {
  local path="$1"
  if [[ ! -f "$path" ]]; then
    echo "Missing required file: $path" >&2
    exit 1
  fi
}

stop_stale_server() {
  local jar_path="$SERVER_DIR/$SERVER_JAR_NAME"
  pkill -f "$jar_path" >/dev/null 2>&1 || true
}

find_open_port() {
  local start_port="$1"
  local end_port=$((start_port + 200))
  local candidate
  for ((candidate = start_port; candidate <= end_port; candidate++)); do
    if ! ss -ltnH "( sport = :$candidate )" | grep -q .; then
      echo "$candidate"
      return 0
    fi
  done
  echo "Failed to find an open port in range ${start_port}-${end_port}" >&2
  exit 1
}

wait_for_log() {
  local pattern="$1"
  local deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
  while (( SECONDS < deadline )); do
    if [[ -f "$LOG_FILE" ]] && rg -q "$pattern" "$LOG_FILE"; then
      return 0
    fi
    sleep 1
  done
  echo "Timed out waiting for log pattern: $pattern" >&2
  if [[ -f "$LOG_FILE" ]]; then
    tail -n 80 "$LOG_FILE" >&2 || true
  fi
  return 1
}

wait_for_server_ready() {
  local done_deadline=$((SECONDS + READY_TIMEOUT_SECONDS))
  while (( SECONDS < done_deadline )); do
    if [[ -f "$LOG_FILE" ]] && rg -q 'Done \([0-9.]+s\)! For help, type "help"' "$LOG_FILE"; then
      break
    fi
    sleep 1
  done

  if ! [[ -f "$LOG_FILE" ]] || ! rg -q 'Done \([0-9.]+s\)! For help, type "help"' "$LOG_FILE"; then
    echo "Timed out waiting for server startup completion" >&2
    tail -n 120 "$LOG_FILE" >&2 || true
    exit 1
  fi

  local ready_deadline=$((SECONDS + 15))
  while (( SECONDS < ready_deadline )); do
    if rg -q "Starting Minecraft server on \\*:${SERVER_PORT}" "$LOG_FILE" && ! rg -q 'FAILED TO BIND TO PORT' "$LOG_FILE"; then
      return 0
    fi
    sleep 1
  done

  echo "Server did not bind cleanly to port ${SERVER_PORT}" >&2
  tail -n 120 "$LOG_FILE" >&2 || true
  exit 1
}

send_command() {
  printf '%s\n' "$1" >&${FIFO_FD}
}

start_server() {
  rm -f "$LOG_FILE" "$FIFO_PATH"
  mkfifo "$FIFO_PATH"
  exec {FIFO_FD}<>"$FIFO_PATH"

  cd "$SERVER_DIR"
  env JAVA_HOME="$JAVA_HOME" PATH="$JAVA_HOME/bin:$PATH" \
    timeout --signal=TERM --kill-after=20s "${TIMEOUT_SECONDS}s" \
    "$JAVA_BIN" -jar "$SERVER_JAR_NAME" nogui --universe "$UNIVERSE_DIR" <"$FIFO_PATH" >/dev/null 2>&1 &
  SERVER_PID=$!

  wait_for_server_ready
}

stop_server() {
  send_command 'stop'
  wait "$SERVER_PID"
  SERVER_PID=""
  exec {FIFO_FD}>&- || true
  unset FIFO_FD
  rm -f "$FIFO_PATH"
}

require_file "$JAVA_BIN"
require_file "$SERVER_JAR_SOURCE"
require_file "$MTR_JAR_SOURCE"
require_file "$MOD_JAR"

stop_stale_server
SERVER_PORT="${SERVER_PORT:-$(find_open_port "$DEFAULT_SERVER_PORT")}"

mkdir -p "$SERVER_DIR/mods"
mkdir -p "$UNIVERSE_DIR"
cp "$SERVER_JAR_SOURCE" "$SERVER_DIR/$SERVER_JAR_NAME"
cp "$MTR_JAR_SOURCE" "$SERVER_DIR/mods/$(basename "$MTR_JAR_SOURCE")"
cp "$MOD_JAR" "$SERVER_DIR/mods/$(basename "$MOD_JAR")"
printf 'eula=true\n' >"$SERVER_DIR/eula.txt"
cat >"$SERVER_DIR/server.properties" <<EOF
server-port=${SERVER_PORT}
online-mode=false
enable-rcon=false
enable-query=false
motd=Russian Metro Smoke
level-name=${LEVEL_NAME}
EOF
rm -f "$LOG_FILE" "$FIFO_PATH"
start_server

send_command 'forceload add 0 0 48 16'
wait_for_log 'Marked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'

send_command 'fill 0 63 0 40 66 0 air'
wait_for_log 'Successfully filled [0-9]+ block\(s\)|No blocks were filled'

send_command 'fill 0 63 0 40 63 0 stone'
wait_for_log 'Successfully filled 41 block\(s\)'

send_command 'setblock 0 64 0 russianmetro:moscow_old_ticket_barrier_entrance[facing=north,open=closed]'
wait_for_log 'Changed the block at 0, 64, 0'

send_command 'setblock 1 64 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=lower,side=left,end=false,unlocked=false]'
wait_for_log 'Changed the block at 1, 64, 0'
send_command 'setblock 1 65 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=upper,side=left,end=false,unlocked=false]'
wait_for_log 'Changed the block at 1, 65, 0'
send_command 'setblock 2 64 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=lower,side=right,end=false,unlocked=false]'
wait_for_log 'Changed the block at 2, 64, 0'
send_command 'setblock 2 65 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=upper,side=right,end=false,unlocked=false]'
wait_for_log 'Changed the block at 2, 65, 0'

send_command 'setblock 3 64 0 russianmetro:moscow_old_ticket_barrier_side_cover[facing=north]'
wait_for_log 'Changed the block at 3, 64, 0'

send_command 'setblock 4 64 0 russianmetro:moscow_new_ticket_barrier_entrance[facing=north,open=closed]'
wait_for_log 'Changed the block at 4, 64, 0'

send_command 'setblock 5 64 0 russianmetro:moscow_new_ticket_barrier_exit[facing=north,open=closed]'
wait_for_log 'Changed the block at 5, 64, 0'

send_command 'setblock 6 64 0 russianmetro:moscow_new_ticket_barrier_side_cover[facing=north]'
wait_for_log 'Changed the block at 6, 64, 0'

send_command 'setblock 7 64 0 russianmetro:moscow_metro_logo[facing=north]'
wait_for_log 'Changed the block at 7, 64, 0'

send_command 'setblock 8 64 0 russianmetro:train_stop_sign[facing=north]'
wait_for_log 'Changed the block at 8, 64, 0'

send_command 'setblock 9 64 0 russianmetro:train_stop_sign_1[facing=north]'
wait_for_log 'Changed the block at 9, 64, 0'

send_command 'setblock 10 64 0 russianmetro:train_stop_sign_2[facing=north]'
wait_for_log 'Changed the block at 10, 64, 0'

send_command 'setblock 11 64 0 russianmetro:train_stop_sign_3[facing=north]'
wait_for_log 'Changed the block at 11, 64, 0'

send_command 'setblock 12 64 0 russianmetro:train_stop_sign_4[facing=north]'
wait_for_log 'Changed the block at 12, 64, 0'

send_command 'setblock 13 64 0 russianmetro:train_stop_sign_5[facing=north]'
wait_for_log 'Changed the block at 13, 64, 0'

send_command 'setblock 14 64 0 russianmetro:train_stop_sign_6[facing=north]'
wait_for_log 'Changed the block at 14, 64, 0'

send_command 'setblock 15 64 0 russianmetro:train_stop_sign_7[facing=north]'
wait_for_log 'Changed the block at 15, 64, 0'

send_command 'setblock 16 64 0 russianmetro:train_stop_sign_8[facing=north]'
wait_for_log 'Changed the block at 16, 64, 0'

send_command 'setblock 17 64 0 russianmetro:train_stop_sign_9[facing=north]'
wait_for_log 'Changed the block at 17, 64, 0'

send_command 'setblock 18 64 0 russianmetro:spb_horizontal_elevator_door_odd[facing=north,half=lower,end=false,unlocked=false]'
wait_for_log 'Changed the block at 18, 64, 0'
send_command 'setblock 18 65 0 russianmetro:spb_horizontal_elevator_door_odd[facing=north,half=upper,end=false,unlocked=false]'
wait_for_log 'Changed the block at 18, 65, 0'

send_command 'execute if block 1 64 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=lower,side=left] run say russian-spb-door-lower-left-ok'
wait_for_log 'russian-spb-door-lower-left-ok'
send_command 'execute if block 1 65 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=upper,side=left] run say russian-spb-door-upper-left-ok'
wait_for_log 'russian-spb-door-upper-left-ok'
send_command 'execute if block 2 64 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=lower,side=right] run say russian-spb-door-lower-right-ok'
wait_for_log 'russian-spb-door-lower-right-ok'
send_command 'execute if block 2 65 0 russianmetro:spb_horizontal_elevator_door[facing=north,half=upper,side=right] run say russian-spb-door-upper-right-ok'
wait_for_log 'russian-spb-door-upper-right-ok'
send_command 'execute if block 18 64 0 russianmetro:spb_horizontal_elevator_door_odd run say russian-spb-door-odd-lower-ok'
send_command 'execute if block 18 64 0 russianmetro:spb_horizontal_elevator_door run say russian-spb-door-odd-lower-became-even'
send_command 'execute if block 18 64 0 air run say russian-spb-door-odd-lower-became-air'
wait_for_log 'russian-spb-door-odd-lower-(ok|became-even|became-air)'
send_command 'execute if block 18 65 0 russianmetro:spb_horizontal_elevator_door_odd run say russian-spb-door-odd-upper-ok'
send_command 'execute if block 18 65 0 russianmetro:spb_horizontal_elevator_door run say russian-spb-door-odd-upper-became-even'
send_command 'execute if block 18 65 0 air run say russian-spb-door-odd-upper-became-air'
wait_for_log 'russian-spb-door-odd-upper-(ok|became-even|became-air)'

send_command 'execute if block 0 64 0 russianmetro:moscow_old_ticket_barrier_entrance[facing=north,open=closed] run say russian-old-entrance-ok'
wait_for_log 'russian-old-entrance-ok'

send_command 'fill 1 64 0 2 65 0 air replace russianmetro:spb_horizontal_elevator_door'
wait_for_log 'Successfully filled [14] block\(s\)|No blocks were filled'
send_command 'execute if block 1 64 0 air if block 1 65 0 air if block 2 64 0 air if block 2 65 0 air run say russian-spb-door-ok'
wait_for_log 'russian-spb-door-ok'

send_command 'execute if block 3 64 0 russianmetro:moscow_old_ticket_barrier_side_cover[facing=north] run say russian-old-side-ok'
wait_for_log 'russian-old-side-ok'

send_command 'execute if block 4 64 0 russianmetro:moscow_new_ticket_barrier_entrance[facing=north,open=closed] run say russian-new-entrance-ok'
wait_for_log 'russian-new-entrance-ok'

send_command 'execute if block 5 64 0 russianmetro:moscow_new_ticket_barrier_exit[facing=north,open=closed] run say russian-new-exit-ok'
wait_for_log 'russian-new-exit-ok'

send_command 'execute if block 6 64 0 russianmetro:moscow_new_ticket_barrier_side_cover[facing=north] run say russian-new-side-ok'
wait_for_log 'russian-new-side-ok'

send_command 'execute if block 7 64 0 russianmetro:moscow_metro_logo[facing=north] run say russian-logo-ok'
wait_for_log 'russian-logo-ok'

send_command 'execute if block 8 64 0 russianmetro:train_stop_sign[facing=north] run say russian-stop-sign-base-ok'
wait_for_log 'russian-stop-sign-base-ok'

send_command 'execute if block 9 64 0 russianmetro:train_stop_sign_1[facing=north] run say russian-stop-sign-1-ok'
wait_for_log 'russian-stop-sign-1-ok'

send_command 'execute if block 10 64 0 russianmetro:train_stop_sign_2[facing=north] run say russian-stop-sign-2-ok'
wait_for_log 'russian-stop-sign-2-ok'

send_command 'execute if block 11 64 0 russianmetro:train_stop_sign_3[facing=north] run say russian-stop-sign-3-ok'
wait_for_log 'russian-stop-sign-3-ok'

send_command 'execute if block 12 64 0 russianmetro:train_stop_sign_4[facing=north] run say russian-stop-sign-4-ok'
wait_for_log 'russian-stop-sign-4-ok'

send_command 'execute if block 13 64 0 russianmetro:train_stop_sign_5[facing=north] run say russian-stop-sign-5-ok'
wait_for_log 'russian-stop-sign-5-ok'

send_command 'execute if block 14 64 0 russianmetro:train_stop_sign_6[facing=north] run say russian-stop-sign-6-ok'
wait_for_log 'russian-stop-sign-6-ok'

send_command 'execute if block 15 64 0 russianmetro:train_stop_sign_7[facing=north] run say russian-stop-sign-7-ok'
wait_for_log 'russian-stop-sign-7-ok'

send_command 'execute if block 16 64 0 russianmetro:train_stop_sign_8[facing=north] run say russian-stop-sign-8-ok'
wait_for_log 'russian-stop-sign-8-ok'

send_command 'execute if block 17 64 0 russianmetro:train_stop_sign_9[facing=north] run say russian-stop-sign-9-ok'
wait_for_log 'russian-stop-sign-9-ok'

send_command 'fill 18 64 0 18 65 0 air replace russianmetro:spb_horizontal_elevator_door_odd'
wait_for_log 'Successfully filled [12] block\(s\)|No blocks were filled'
send_command 'execute if block 18 64 0 air if block 18 65 0 air run say russian-spb-door-odd-ok'
wait_for_log 'russian-spb-door-odd-ok'

send_command 'setblock 30 64 0 russianmetro:moscow_new_ticket_machine[facing=north,half=lower]'
wait_for_log 'Changed the block at 30, 64, 0'

send_command 'setblock 30 65 0 russianmetro:moscow_new_ticket_machine[facing=north,half=upper]'
wait_for_log 'Changed the block at 30, 65, 0'

send_command 'setblock 32 64 0 russianmetro:moscow_old_infosos_stand[facing=north,third=lower]'
wait_for_log 'Changed the block at 32, 64, 0'

send_command 'setblock 32 65 0 russianmetro:moscow_old_infosos_stand[facing=north,third=middle]'
wait_for_log 'Changed the block at 32, 65, 0'

send_command 'setblock 32 66 0 russianmetro:moscow_old_infosos_stand[facing=north,third=upper]'
wait_for_log 'Changed the block at 32, 66, 0'

send_command 'execute if block 30 64 0 russianmetro:moscow_new_ticket_machine[facing=north,half=lower] run say russian-ticket-machine-lower-ok'
wait_for_log 'russian-ticket-machine-lower-ok'

send_command 'execute if block 30 65 0 russianmetro:moscow_new_ticket_machine[facing=north,half=upper] run say russian-ticket-machine-upper-ok'
wait_for_log 'russian-ticket-machine-upper-ok'

send_command 'execute if block 32 64 0 russianmetro:moscow_old_infosos_stand[facing=north,third=lower] run say russian-infosos-lower-ok'
wait_for_log 'russian-infosos-lower-ok'

send_command 'execute if block 32 65 0 russianmetro:moscow_old_infosos_stand[facing=north,third=middle] run say russian-infosos-middle-ok'
wait_for_log 'russian-infosos-middle-ok'

send_command 'execute if block 32 66 0 russianmetro:moscow_old_infosos_stand[facing=north,third=upper] run say russian-infosos-upper-ok'
wait_for_log 'russian-infosos-upper-ok'

send_command 'forceload remove 0 0 48 16'
wait_for_log 'Unmarked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'

stop_server

start_server
send_command 'forceload add 0 0 48 16'
wait_for_log 'Marked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'
send_command 'execute if block 0 64 0 russianmetro:moscow_old_ticket_barrier_entrance[facing=north,open=closed] run say russian-old-entrance-persisted-ok'
wait_for_log 'russian-old-entrance-persisted-ok'
send_command 'execute if block 4 64 0 russianmetro:moscow_new_ticket_barrier_entrance[facing=north,open=closed] run say russian-new-entrance-persisted-ok'
wait_for_log 'russian-new-entrance-persisted-ok'
send_command 'execute if block 5 64 0 russianmetro:moscow_new_ticket_barrier_exit[facing=north,open=closed] run say russian-new-exit-persisted-ok'
wait_for_log 'russian-new-exit-persisted-ok'
send_command 'execute if block 30 64 0 russianmetro:moscow_new_ticket_machine[facing=north,half=lower] run say russian-ticket-machine-lower-persisted-ok'
wait_for_log 'russian-ticket-machine-lower-persisted-ok'
send_command 'execute if block 30 65 0 russianmetro:moscow_new_ticket_machine[facing=north,half=upper] run say russian-ticket-machine-upper-persisted-ok'
wait_for_log 'russian-ticket-machine-upper-persisted-ok'
send_command 'execute if block 32 64 0 russianmetro:moscow_old_infosos_stand[facing=north,third=lower] run say russian-infosos-lower-persisted-ok'
wait_for_log 'russian-infosos-lower-persisted-ok'
send_command 'execute if block 32 65 0 russianmetro:moscow_old_infosos_stand[facing=north,third=middle] run say russian-infosos-middle-persisted-ok'
wait_for_log 'russian-infosos-middle-persisted-ok'
send_command 'execute if block 32 66 0 russianmetro:moscow_old_infosos_stand[facing=north,third=upper] run say russian-infosos-upper-persisted-ok'
wait_for_log 'russian-infosos-upper-persisted-ok'
send_command 'forceload remove 0 0 48 16'
wait_for_log 'Unmarked [0-9]+ chunk[s]? in Overworld from|No chunks were marked for force loading'

stop_server

echo "Russian Metro server smoke passed. Log: $LOG_FILE"
