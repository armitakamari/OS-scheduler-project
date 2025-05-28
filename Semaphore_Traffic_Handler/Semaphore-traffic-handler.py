import threading
import time
import queue
 
LANE_QUANTUMS = {"north": 2, "east": 1, "south": 3, "west": 4}

current_lane = 0  
lane_lock = threading.Lock()
lane_to_direction = {0: "north", 1: "east", 2: "south", 3: "west"}

 
emergency_queue = queue.Queue()
 
path_locks = {direction: threading.Lock() for direction in lane_to_direction.values()} # در اصل 4 تا لاک درست میکنیم 

 
def lane_scheduler():
    global current_lane
    while True:
        with lane_lock:  
            if not emergency_queue.empty():
                emergency_vehicle = emergency_queue.get()
                current_lane = emergency_vehicle.lane
                print(f"Emergency vehicle {emergency_vehicle.vehicle_id} is prioritized on lane {current_lane}!")
            else:
                current_lane = (current_lane + 1) % 4
        time.sleep(LANE_QUANTUMS[lane_to_direction[current_lane]])

 
def can_enter_intersection(vehicle):
    direction = lane_to_direction[vehicle.lane]
    with lane_lock:  
        if vehicle.vehicle_type == "Emergency":
            emergency_queue.put(vehicle)
            return True   
        return (direction == lane_to_direction[current_lane]) and not path_locks[direction].locked()

 
def enter_intersection(vehicle):
    direction = lane_to_direction[vehicle.lane]
    with path_locks[direction]:   
        print(f"Vehicle {vehicle.vehicle_id} entering intersection...")
        time.sleep(vehicle.crossing_time)
        print(f"Vehicle {vehicle.vehicle_id} exited intersection.")

class Vehicle(threading.Thread):
    def __init__(self, vehicle_id, vehicle_type, lane, crossing_time):
        threading.Thread.__init__(self)
        self.vehicle_id = vehicle_id
        self.vehicle_type = vehicle_type
        self.lane = lane
        self.crossing_time = crossing_time

    def run(self):
        while not can_enter_intersection(self):
            time.sleep(0.1)  
        enter_intersection(self)

vehicles = [
    {"vehicle_id": 1, "vehicle_type": "Regular", "lane": 0, "crossing_time": 2},
    {"vehicle_id": 2, "vehicle_type": "Regular", "lane": 1, "crossing_time": 1},
    {"vehicle_id": 3, "vehicle_type": "Emergency", "lane": 2, "crossing_time": 2},
    {"vehicle_id": 4, "vehicle_type": "Emergency", "lane": 3, "crossing_time": 1}
]

def start_simulation():
    vehicle_threads = [Vehicle(**v) for v in vehicles]
    scheduler_thread = threading.Thread(target=lane_scheduler, daemon=True)
    scheduler_thread.start()
    
    for vehicle in vehicle_threads:
        vehicle.start()
    for vehicle in vehicle_threads:
        vehicle.join()

if __name__ == "__main__":
    print("Starting Intersection Simulation...")
    start_simulation()
    print("Simulation Complete.")
