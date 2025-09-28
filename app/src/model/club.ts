export type Club = {
  id: string
  name: string
  sport: string
  distance: number
  notation: number
  addresse: string
  members: number
  position: [number, number]
  // Données additionnelles (fixtures)
  city?: string
  country?: string
  postalCode?: string
  phoneNumber?: string
  email?: string
  typeId?: string
  typeName?: string
  dateCreation?: string
  description?: string
}

