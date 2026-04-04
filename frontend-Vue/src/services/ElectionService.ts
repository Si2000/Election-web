const API_BASE = 'http://oege.ie.hva.nl:8888';

export const getRegionsStructure = async (electionId: string) => {
  const response = await fetch(`${API_BASE}/${electionId}/regions/structure`);
  console.log('Response status:', response.status);
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

  return await response.json();
};

export const getMunicipalities = async (electionId: string) => {
  const response = await fetch(`${API_BASE}/${electionId}/municipality?folderName=TK2023-Partial/municipality`);

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
  }

  return await response.json();
};

export const getConstituency = async (electionId: string) => {
  const response = await fetch(`${API_BASE}/${electionId}/constituency?folderName=TK2023-Partial/constituency`);

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
  }

  return await response.json();
};

/**
 * Haalt alle partijen op voor een specifieke verkiezing (voor de filter dropdown).
 */
export const getParties = async (electionId: string, folderName: string) => {
  // We gebruiken het bestaande backend endpoint
  const response = await fetch(`${API_BASE}/${electionId}/parties?folderName=${encodeURIComponent(folderName)}`);

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`HTTP error! status: ${response.status}, body: ${text}`);
  }

  return await response.json();
};

/**
 * Haalt alle kandidaten op, optioneel gefilterd op partijnaam.
 */
// export const getAllCandidates = async (electionId: string, folderName: string, year: number, partyName?: string) => {
//
//   // Bouw de URL dynamisch op
//   let url = `${API_BASE}/electionresults/${year}/candidates)}`;
//
//   // Voeg de partyName parameter toe ALS deze is meegegeven
//   if (partyName && partyName.length > 0) {
//     url += `&partyName=${encodeURIComponent(partyName)}`;
//   }
//
//   const response = await fetch(url, { method: 'GET' }); // Blijft POST, zoals in je origineel
//
//   console.log('Response status:', response.status);
//   console.log(Response);
//   if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
//
//   return await response.json();
// };

