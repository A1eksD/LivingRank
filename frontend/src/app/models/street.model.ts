export interface Street {
  id: number;
  streetName: string;
  postalCode: string;
  city: string;
  stateRegion?: string;
  country: string;
  lat?: number;
  lon?: number;
  averageRating?: number;
  reviewCount?: number;
}

export interface StreetDetail {
  street: Street;
  criteriaAverages: CriteriaAverages;
  userHasReviewed: boolean;
}

export interface CriteriaAverages {
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
}
