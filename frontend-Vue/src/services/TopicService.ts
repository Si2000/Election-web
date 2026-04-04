import { userInfo } from '@/storages/userStorage';

const TOPIC_API = 'http://oege.ie.hva.nl:8888/api/topics';
const REPORT_API = 'hhttp://oege.ie.hva.nl:8888/api/reports';
const BASE_URL = 'http://oege.ie.hva.nl:8888/api';

export interface Topic {
  id: number;
  title: string;
  content: string;
  authorName: string;
  createdAt: string;
  likeCount?: number;
}

export interface Comment {
  id: number;
  content: string;
  authorName: string;
  createdAt: string;
  parentId?: number;
  replyToUsername?: string;
}

export interface CommentRequest {
  topicId: number;
  content: string;
  parentId?: number;
  replyToUsername?: string;
}

export async function getCommentsByTopic(topicId: number): Promise<Comment[]> {
  const response = await fetch(`${BASE_URL}/comments/topic/${topicId}`);
  if (!response.ok) {
    throw new Error('Kon comments niet laden');
  }
  return await response.json();
}

export async function addComment(data: CommentRequest, token: string): Promise<Comment> {
  const response = await fetch(`${BASE_URL}/comments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });

  if (!response.ok) {
    throw new Error('Kon comment niet plaatsen');
  }
  return await response.json();
}

export const getAllTopics = async (): Promise<Topic[]> => {
  const response = await fetch(TOPIC_API);
  if (!response.ok) throw new Error('Kon topics niet ophalen');
  return await response.json();
};

export const createTopic = async (title: string, content: string) => {
  const store = userInfo();
  if (!store.token) throw new Error('Niet ingelogd');

  const response = await fetch(TOPIC_API, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${store.token}`,
    },
    body: JSON.stringify({ title, content }),
  });

  if (!response.ok) throw new Error('Kon topic niet aanmaken');
  return await response.json();
};

export const getTopicById = async (id: number): Promise<Topic> => {
  const response = await fetch(`${TOPIC_API}/${id}`);
  if (!response.ok) throw new Error('Kon topic niet ophalen');
  return await response.json();
};

export const reportTopic = async (
  topicId: number,
  token: string,
  reason: string
): Promise<void> => {
  if (!token) throw new Error('Niet ingelogd');

  const response = await fetch(`${REPORT_API}/topics/${topicId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ reason }),
  });

  if (!response.ok) throw new Error('Rapporteren mislukt');
};

export const getTopicLikeCount = async (topicId: number): Promise<number> => {
  const response = await fetch(`${TOPIC_API}/${topicId}/likes/count`);
  if (!response.ok) throw new Error('Kon likes niet ophalen');
  return await response.json();
};

export const hasLikedTopic = async (
  topicId: number,
  token: string
): Promise<boolean> => {
  if (!token) return false;

  const response = await fetch(`${TOPIC_API}/${topicId}/likes/me`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) return false;
  return await response.json();
};

export const likeTopic = async (
  topicId: number,
  token: string
): Promise<void> => {
  if (!token) throw new Error('Niet ingelogd');

  const response = await fetch(`${TOPIC_API}/${topicId}/likes`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error('Liken mislukt');
};

export const getCommentById = async (commentId: number): Promise<Comment> => {
  const response = await fetch(`${BASE_URL}/comments/${commentId}`);
  if (!response.ok) throw new Error("Kon comment niet ophalen");
  return await response.json();
};

export async function reportComment(commentId: number, reason: string): Promise<void> {
  const store = userInfo();
  if (!store.token) throw new Error('Niet ingelogd');

  const response = await fetch(`${REPORT_API}/comments/${commentId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${store.token}`,
    },
    body: JSON.stringify({ reason }),
  });

  if (!response.ok) throw new Error('Rapporteren van comment mislukt');
}

export const unlikeTopic = async (
  topicId: number,
  token: string
): Promise<void> => {
  if (!token) throw new Error('Niet ingelogd');

  const response = await fetch(`${TOPIC_API}/${topicId}/likes`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) throw new Error('Unliken mislukt');
};


/**
 * Updates the content of an existing comment.
 * @param commentId The ID of the comment to update.
 * @param content The new text content.
 * @param token The user's authentication token.
 */
export const updateComment = async (commentId: number, content: string, token: string): Promise<void> => {
  const response = await fetch(`${BASE_URL}/comments/${commentId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ content })
  });

  if (!response.ok) throw new Error('Failed to update comment');
};

/**
 * Deletes a comment permanently.
 * @param commentId The ID of the comment to delete.
 * @param token The user's authentication token.
 */
export const deleteComment = async (commentId: number, token: string): Promise<void> => {
  const response = await fetch(`${BASE_URL}/comments/${commentId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) throw new Error('Failed to delete comment');
};

// NIEUW: Functie om eigen topic te verwijderen
export const deleteTopic = async (topicId: number, token: string): Promise<void> => {
  if (!token) throw new Error('Niet ingelogd');

  const response = await fetch(`${TOPIC_API}/${topicId}`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || 'Verwijderen mislukt');
  }
};
