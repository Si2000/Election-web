const API_ADMIN = "http://oege.ie.hva.nl:8888/api/admin";

const getAuthHeaders = () => ({
  "Content-Type": "application/json",
  "Authorization": `Bearer ${localStorage.getItem("authToken")}`
});
export const getAllUsers = async () => {
  const token = localStorage.getItem("authToken");
  const res = await fetch(`${API_ADMIN}/users`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
  return await res.json();
};

export const getReports = async () => {
  const res = await fetch(`${API_ADMIN}/reports`, {
    method: "GET",
    headers: getAuthHeaders(),
  })
  return await res.json();
}



export const updateRole = async (email: string, role: string) => {
  const res = await fetch(
    `${API_ADMIN}/users/${email}/role?role=${role}`,
    {
      method: "PATCH",
      headers: getAuthHeaders(),
    }
  );

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return await res.text(); // “Role updated”
};

export const deleteReport = async (
  id: number,
  deleteTopic: boolean
) => {
  const res = await fetch(
    `${API_ADMIN}/reports/${id}?deleteTopic=${deleteTopic}`,
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    }
  );

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return await res.text();
};

// Get all banned users
export const getBannedUsers = async () => {
  const res = await fetch(`${API_ADMIN}/banned-users`, {
    method: "GET",
    headers: getAuthHeaders(),
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  // backend returns { users: [...] }
  const data = await res.json();
  return data.users;
};

export const getReportedUsers = async () => {
  const res = await fetch(`${API_ADMIN}/reported-users`, {
    method: "GET",
    headers: getAuthHeaders(),
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return await res.json();
};

export const reportUser = async (reportedUserId: number, reason: string, token?: string) => {
  if (!token) token = localStorage.getItem("authToken")!;

  const res = await fetch(`${API_ADMIN}/reported-users`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({ reportedUserId, reason })
  });

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return await res.json();
}

//Update ban state for a user
export const updateBanState = async (email: string, state: boolean) => {
  const res = await fetch(
    `${API_ADMIN}/users/${email}/ban?state=${state}`,
    {
      method: "PATCH",
      headers: getAuthHeaders(),
    }
  );

  if (!res.ok) {
    throw new Error(await res.text());
  }

  return await res.text(); // "User banned." or "User unbanned."
};
