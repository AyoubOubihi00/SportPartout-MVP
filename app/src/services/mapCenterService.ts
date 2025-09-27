export type Coordinates = [number, number];

class MapCenterService {
  private center: Coordinates = [48.8566, 2.3522];
  private listeners: Array<(center: Coordinates) => void> = [];

  getCenter(): Coordinates {
    return this.center;
  }

  setCenter(newCenter: Coordinates) {
    this.center = newCenter;
    this.listeners.forEach(listener => listener(this.center));
  }

  onCenterChange(listener: (center: Coordinates) => void) {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }
}

export const mapCenterService = new MapCenterService();
