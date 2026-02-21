export interface Review {
  id: number;
  streetId: number;
  overallRating: number;
  dampInHouse?: number;
  friendlyNeighbors?: number;
  houseCondition?: number;
  infrastructureConnections?: number;
  neighborsInGeneral?: number;
  neighborsVolume?: number;
  smellsBad?: number;
  thinWalls?: number;
  noiseFromStreet?: number;
  publicSafetyFeeling?: number;
  cleanlinessSharedAreas?: number;
  parkingSituation?: number;
  publicTransportAccess?: number;
  internetQuality?: number;
  pestIssues?: number;
  heatingReliability?: number;
  waterPressureOrQuality?: number;
  valueForMoney?: number;
  comment?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ReviewRequest {
  overallRating: number;
  dampInHouse?: number;
  friendlyNeighbors?: number;
  houseCondition?: number;
  infrastructureConnections?: number;
  neighborsInGeneral?: number;
  neighborsVolume?: number;
  smellsBad?: number;
  thinWalls?: number;
  noiseFromStreet?: number;
  publicSafetyFeeling?: number;
  cleanlinessSharedAreas?: number;
  parkingSituation?: number;
  publicTransportAccess?: number;
  internetQuality?: number;
  pestIssues?: number;
  heatingReliability?: number;
  waterPressureOrQuality?: number;
  valueForMoney?: number;
  comment?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const CRITERIA_LABELS: Record<string, string> = {
  dampInHouse: 'Feuchtigkeit im Haus',
  friendlyNeighbors: 'Freundliche Nachbarn',
  houseCondition: 'Hauszustand',
  infrastructureConnections: 'Infrastruktur-Anbindung',
  neighborsInGeneral: 'Nachbarn allgemein',
  neighborsVolume: 'Lautstärke der Nachbarn',
  smellsBad: 'Geruchsbelästigung',
  thinWalls: 'Dünne Wände',
  noiseFromStreet: 'Straßenlärm',
  publicSafetyFeeling: 'Sicherheitsgefühl',
  cleanlinessSharedAreas: 'Sauberkeit Gemeinschaftsflächen',
  parkingSituation: 'Parksituation',
  publicTransportAccess: 'ÖPNV-Anbindung',
  internetQuality: 'Internetqualität',
  pestIssues: 'Ungeziefer',
  heatingReliability: 'Heizung Zuverlässigkeit',
  waterPressureOrQuality: 'Wasser Druck/Qualität',
  valueForMoney: 'Preis-Leistung'
};
