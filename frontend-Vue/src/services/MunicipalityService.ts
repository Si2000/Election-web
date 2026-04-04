const API_BASE = 'http://oege.ie.hva.nl:8888';

export interface ResultNodeDTO {
  id: string;
  name: string;
  category: string;
  totalVotes: number;
  votesPerParty: Record<string, number>;
  children: ResultNodeDTO[];
}

export interface AggregatedCandidateDTO {
  id: string;
  name: string;
  partyName: string;
  votes: number;
  totalVotes: number;
  percentage: number;
}

export interface Candidate {
  candidateid: string;
  firstname: string;
  lastname: string;
  party: string;
  gender: string;

}


export const getNationalResults = async (year: number): Promise<ResultNodeDTO> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getMunicipalityNode = async (year: number, id: string): Promise<ResultNodeDTO> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/municipalities/${id}`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getConstituencyNode = async (year: number, id: string): Promise<ResultNodeDTO> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/constituencies/${id}`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};
export const getCandidatesForNational = async (year: number): Promise<AggregatedCandidateDTO[]> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/national/candidates`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getCandidatesForMunicipality = async (year: number, id: string): Promise<AggregatedCandidateDTO[]> => {
  const safeId = encodeURIComponent(id);
  const response = await fetch(`${API_BASE}/electionresults/${year}/municipalities/${safeId}/candidates`);
  if (!response.ok) {
    console.error(`Error fetching candidates for ${id}:`, response.statusText);
    return [];
  }

  return await response.json();
};
export const getCandidatesForConstituency = async (year: number, id: string): Promise<AggregatedCandidateDTO[]> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/constituencies/${id}/candidates`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getCandidatesForPollingStation = async (year: number, id: string): Promise<AggregatedCandidateDTO[]> => {
  const encodedId = encodeURIComponent(id);
  const response = await fetch(`${API_BASE}/electionresults/${year}/pollingstations/${encodedId}/candidates`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getAllMunicipalities = async (year: number): Promise<ResultNodeDTO[]> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/municipalities`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export  const getAllConstituency = async (year: number): Promise<ResultNodeDTO[]> => {
  const response = await fetch(`${API_BASE}/electionresults/${year}/constituencies`);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
};

export const getAllCandidates = async (year: number, party?: string, gender?: string): Promise<Candidate[]> => {
  const url = new URL(`${API_BASE}/electionresults/${year}/candidates`);

  if (party) {
    url.searchParams.append('party', party);
  }
  if (gender) {
    url.searchParams.append('gender', gender);
  }

  const response = await fetch(url.toString());
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
  return await response.json();
}

export const searchByPostcode = async (year: number, postcode: string): Promise<ResultNodeDTO[]> => {
  const Postcode = postcode.replace(/\s+/g, '');
  const response = await fetch(`${API_BASE}/electionresults/${year}/postcode/${Postcode}`);

  if (response.status === 404) {
    return [];
  }
  if (!response.ok) {
    throw new Error('Fout bij zoeken op postcode');
  }
  return await response.json();
};

export const searchByPostcodeRange = async (year: number, start: string, end: string): Promise<ResultNodeDTO[]> => {
  const s = start.replace(/\s+/g, '');
  const e = end.replace(/\s+/g, '');

  const response = await fetch(`${API_BASE}/electionresults/${year}/postcoderange?start=${s}&end=${e}`);

  if (response.status === 404) return [];
  if (!response.ok) throw new Error('Range search failed');

  return await response.json();
};
