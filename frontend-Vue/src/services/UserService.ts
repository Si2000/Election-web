const API_BASE = 'http://oege.ie.hva.nl:8888/api/user';

// Helper voor de headers (voegt token toe)
const getAuthHeaders = () => {
  const token = localStorage.getItem('authToken');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
};

export const getPublicUserProfile = async (username: string) => {
  const response = await fetch(`${API_BASE}/profile/by-username/${username}`, {
    method: "GET",
  });

  if (!response.ok) throw new Error("User not found");
  return await response.json();
};

const PROFILE_BASE = 'http://oege.ie.hva.nl:8888/api/profile';

// Haal topics van een gebruiker (public)
export async function getUserTopics(userId: number) {
  const res = await fetch(`${PROFILE_BASE}/${userId}/topics`);
  if (!res.ok) throw new Error("Topics not found");
  return res.json();
}

// Haal comments van een gebruiker (public)
export async function getUserComments(userId: number) {
  const res = await fetch(`${PROFILE_BASE}/${userId}/comments`);
  if (!res.ok) throw new Error("Comments not found");
  return res.json();
}



export const getUserProfile = async () => {
  const response = await fetch(`${API_BASE}/profile`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });
  if (!response.ok) throw new Error('Kon profiel niet laden');
  return await response.json();
};

export const updateUserProfile = async (username: string, email: string) => {
  const response = await fetch(`${API_BASE}/profile`, {
    method: 'PUT',
    headers: getAuthHeaders(),
    body: JSON.stringify({ username, email }),
  });

  if (!response.ok) {
    const text = await response.text(); // Haal de error message van backend (bv. "Naam bezet")
    throw new Error(text);
  }
  return await response.json();
};

export const changeUserPassword = async (oldPassword: string, newPassword: string) => {
  const response = await fetch(`${API_BASE}/change-password`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify({ oldPassword, newPassword }),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text);
  }
  return await response.json();
};

export const deleteUserAccount = async () => {
  const response = await fetch(`${API_BASE}/delete`, {
    method: 'DELETE',
    headers: getAuthHeaders(),
  });

  if (!response.ok) throw new Error('Kon account niet verwijderen');
  return await response.json();
};
export const getPersona = async () => {
  const res = await fetch(`${API_BASE}/persona`, {
    method: 'GET',
    headers: getAuthHeaders(),
  });

  if (!res.ok) throw new Error("Persona kan niet geladen worden");
  return await res.json();
};


