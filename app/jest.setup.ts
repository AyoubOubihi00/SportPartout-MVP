import { TextEncoder, TextDecoder } from "util";

globalThis.TextEncoder = TextEncoder;
globalThis.TextDecoder = TextDecoder as typeof globalThis.TextDecoder;

if (typeof globalThis.fetch === "undefined") {
  globalThis.fetch = async () => {
    const body = {
      data: {
        sportTypes: [
          { id: "1", name: "Tennis" },
          { id: "2", name: "Football" },
          { id: "3", name: "Basket" },
        ],
      },
    };

    return {
      ok: true,
      status: 200,
      json: async () => body,
      text: async () => JSON.stringify(body),
    } as Response;
  };
}
