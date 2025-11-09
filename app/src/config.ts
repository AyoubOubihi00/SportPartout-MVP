// src/config.ts

export const API_BASE_URL = "http://localhost:8080";

// Base URL for the BFF exposed through Envoy
// Example full GraphQL endpoint: `${BFF_BASE_URL}/graphql`
export const BFF_BASE_URL = `${API_BASE_URL}/bff`;