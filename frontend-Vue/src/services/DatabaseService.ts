const API_ROOT = 'http://oege.ie.hva.nl:8888';


export const getAllCandidates = async (year: number) => {
  const response = await fetch(`${API_ROOT}/elections/${year}`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getPartyVotes = async (partyName?: string, municipalityName?: string) => {
  const url = new URL(`${API_ROOT}/api/party-votes`);
  if (partyName) {
    url.searchParams.append('partyName', partyName);
  }
  if (municipalityName) {
    url.searchParams.append('municipality', municipalityName);
  }
  const response = await fetch(url.toString());
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();

};

export const getPartyFilterOptions = async (year: number) => {
  const url = new URL(`${API_ROOT}/api/party-votes/parties`);
  url.searchParams.append('year', year.toString());

  const response = await fetch(url.toString());
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getMunicipalityFilterOptions = async (year: number) => {
  const url = new URL(`${API_ROOT}/api/party-votes/municipalities`);
  url.searchParams.append('year', year.toString());

  const response = await fetch(url.toString());
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();}

export interface PartyWinnerDTO {
  place: string;
  gemeente: string;
  partyName: string;
  totalVotes: number;
  losers?: Record<string, number>;
}

export interface MunicipalityPartyResultDTO {
  id: string;       // gemeente-ID
  name: string;     // naam van de gemeente
  party: string;    // partijnaam
  votes: number;    // aantal stemmen
}

export const getWinnersByYear = async (year: number): Promise<MunicipalityPartyResultDTO[]> => {
  const url = `${API_ROOT}/electionresults/${year}/municipalities/winners`;
  console.log("Fetching winners from:", url);

  const response = await fetch(url);
  if (!response.ok) {
    const text = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
  }

  const data = await response.json();
  console.log("Winners ontvangen:", data.length, "records");
  return data;
};


export const getProvinceWinnersByYear = async (year: number): Promise<PartyWinnerDTO[]> => {
  const url = `${API_ROOT}/electionresults/${year}/provinces/winners`;
  console.log("Fetching province winners from:", url);

  const response = await fetch(url);
  if (!response.ok) {
    const text = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
  }

  const data: PartyWinnerDTO[] = await response.json();
  console.log("Province winners ontvangen:", data.length, "records");
  return data;
};

export const getAllKieskringVote = async () => {
  const response = await fetch(`${API_ROOT}/api/party-votes/kieskring`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
}
