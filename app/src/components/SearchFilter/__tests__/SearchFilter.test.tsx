import "@testing-library/jest-dom";
import { render, screen, fireEvent } from "@testing-library/react";
import { act } from "react";
import * as cityApiService from "../../../services/cityApiService";
import { SearchFilter } from "../SearchFilter";
import * as mapCenterService from "../../../services/mapCenterService";
import * as sportService from "../../../services/sportService";

jest.mock("../../../services/sportService");
jest.mock("../../../services/mapCenterService");

const mockSports = [
  { id: "1", name: "Basket", icon: "basket-icon" },
  { id: "2", name: "Foot", icon: "foot-icon" },
];

jest.spyOn(sportService, "getSportsByName").mockResolvedValue(mockSports as any);

jest
  .spyOn(mapCenterService.mapCenterService, "setCenter")
  .mockImplementation(jest.fn());
jest
  .spyOn(mapCenterService.mapCenterService, "onCenterChange")
  .mockImplementation(jest.fn());

describe("SearchFilter", () => {
  it("appelle setCenter du service quand une ville est sélectionnée", async () => {
    const city = {
      id: "nancy",
      name: "Nancy",
      postalCode: "54000",
      coordinates: [48.692054, 6.184417] as [number, number],
    };

    jest.spyOn(cityApiService, "fetchCities").mockResolvedValue([city]);

    render(<SearchFilter />);
    const input = screen.getByRole("textbox");

    // open the dropdown by focusing the input then trigger the search
    fireEvent.focus(input);
    await act(async () => {
      fireEvent.change(input, { target: { value: "Nancy" } });
    });

    // wait for the city option to appear and click it
    const cityOption = await screen.findByText("Nancy");
    await act(async () => {
      fireEvent.click(cityOption);
    });

    expect(mapCenterService.mapCenterService.setCenter).toHaveBeenCalledWith([
      48.692054,
      6.184417,
    ]);
  });

  // Ajoute d'autres tests pour le style, le thème, etc.
});
