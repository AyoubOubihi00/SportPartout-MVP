import { mapCenterService } from "../mapCenterService";

describe("mapCenterService", () => {
  it("getCenter retourne le centre courant", () => {
    expect(mapCenterService.getCenter()).toEqual([48.8566, 2.3522]);
  });

  it("setCenter met à jour le centre et notifie les listeners", () => {
    const listener = jest.fn();
    mapCenterService.onCenterChange(listener);
    mapCenterService.setCenter([1, 2]);
    expect(mapCenterService.getCenter()).toEqual([1, 2]);
    expect(listener).toHaveBeenCalledWith([1, 2]);
  });
});
