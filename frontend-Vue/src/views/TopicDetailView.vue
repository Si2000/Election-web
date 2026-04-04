<script setup lang="ts">
import '@/styles/topicDetail.css'; // <--- HIER importeren we de CSS
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { userInfo } from '@/storages/userStorage';
import {
  getTopicById,
  reportTopic,
  reportComment,
  getTopicLikeCount,
  hasLikedTopic,
  likeTopic,
  unlikeTopic,
  getCommentsByTopic,
  addComment,
  updateComment,
  deleteComment,
  deleteTopic,
  type Topic,
  type Comment
} from '@/services/TopicService';

/**
 * TopicDetail View
 * Displays a topic, allows interaction (like, report) and full comment management (Create, Read, Update, Delete).
 */

const route = useRoute();
const router = useRouter();
const store = userInfo();

const topic = ref<Topic | null>(null);
const loading = ref(true);
const error = ref('');

// Like State
const likeCount = ref(0);
const likedByMe = ref(false);
const likeBusy = ref(false);

// Comment State
const comments = ref<Comment[]>([]);
const newCommentContent = ref('');
const commentSubmitting = ref(false);
const commentError = ref('');

// Reply State
const reply = ref<Comment | null>(null);
const expandedComments = ref(new Set<number>());

const editingCommentId = ref<number | null>(null);
const editContent = ref('');

const getRootComments = () => comments.value.filter(c => !c.parentId);
const getReplies = (parentId: number) => comments.value.filter(c => c.parentId === parentId);

const toggleReplies = (commentId: number) => {
  if (expandedComments.value.has(commentId)) expandedComments.value.delete(commentId);
  else expandedComments.value.add(commentId);
};

const formatDate = (dateString: string) => {
  if (!dateString) return '';

  const dateToParse = dateString.endsWith('Z') ? dateString : dateString + 'Z';

  return new Date(dateToParse).toLocaleDateString('nl-NL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};


const setReply = (comment: Comment) => {
  cancelEdit();
  reply.value = comment;
  newCommentContent.value = '';
  commentError.value = '';
};

const cancelReply = () => {
  reply.value = null;
  newCommentContent.value = '';
  commentError.value = '';
};

/**
 * Enters edit mode for a specific comment.
 */
const startEdit = (comment: Comment) => {
  cancelReply(); // Sluit reply mode als die open staat
  editingCommentId.value = comment.id;
  editContent.value = comment.content;
};

/**
 * Cancels edit mode.
 */
const cancelEdit = () => {
  editingCommentId.value = null;
  editContent.value = '';
};

/**
 * Saves changes to the backend and updates local state.
 */
const handleUpdateComment = async (commentId: number) => {
  if (!store.token || !editContent.value.trim()) return;

  try {
    await updateComment(commentId, editContent.value, store.token);

    // Update de tekst in de lokale lijst zonder refresh
    const target = comments.value.find(c => c.id === commentId);
    if (target) {
      target.content = editContent.value;
    }
    cancelEdit();
  } catch (e) {
    alert("Kon bericht niet bewerken.");
  }
};

/**
 * Deletes a comment after confirmation.
 */
const handleDeleteComment = async (commentId: number) => {
  if (!confirm("Weet je zeker dat je dit bericht wilt verwijderen?")) return;
  if (!store.token) return;

  try {
    await deleteComment(commentId, store.token);
    // Verwijder uit de lokale lijst
    comments.value = comments.value.filter(c => c.id !== commentId);
  } catch (e) {
    alert("Kon bericht niet verwijderen.");
  }
};

// --- DATA FETCHING ---
onMounted(async () => {
  const id = Number(route.params.id);
  try {
    topic.value = await getTopicById(id);

    try {
      likeCount.value = await getTopicLikeCount(id);
      if (store.isLoggedIn && store.token) {
        likedByMe.value = await hasLikedTopic(id, store.token);
      }
    } catch (e) { console.warn("Could not load likes"); }

    try {
      comments.value = await getCommentsByTopic(id);
    } catch (e) { console.warn("Could not load comments"); }

  } catch (e) {
    error.value = "Kon discussie niet laden.";
  } finally {
    loading.value = false;
  }
});

// --- ACTIONS: REPORT & LIKE ---
const handleReport = async () => {
  if (!topic.value) return;

  // Deze check lost de error op:
  if (!store.token) {
    alert("Je moet ingelogd zijn om te rapporteren.");
    return;
  }

  const reason = prompt("Waarom wil je deze discussie rapporteren?");
  if (!reason || reason.trim() === "") return;

  try {
    // TypeScript weet nu dat store.token zeker een string is
    await reportTopic(topic.value.id, store.token, reason);
    alert("Bedankt! Je melding is ontvangen.");
  } catch (e) {
    alert("Er ging iets mis bij het versturen van de melding.");
  }
};

const handleReportComment = async (comment: Comment) => {
  const reason = prompt("Waarom wil je deze reactie rapporteren?");
  if (!reason || reason.trim() === '') return;

  try {
    await reportComment(comment.id, reason);
    alert("Reactie gerapporteerd. Bedankt voor je melding.");
  } catch (e) {
    alert("Er ging iets mis bij het rapporteren van de reactie.");
  }
};

const handleLikeToggle = async () => {
  if (!topic.value) return;
  if (!store.isLoggedIn || !store.token) {
    alert('Log in om deze discussie te liken.');
    return;
  }
  if (likeBusy.value) return;

  likeBusy.value = true;
  try {
    if (likedByMe.value) {
      await unlikeTopic(topic.value.id, store.token);
      likedByMe.value = false;
      likeCount.value = Math.max(0, likeCount.value - 1);
    } else {
      await likeTopic(topic.value.id, store.token);
      likedByMe.value = true;
      likeCount.value += 1;
    }
  } catch (e) {
    alert('Er ging iets mis bij het liken.');
  } finally {
    likeBusy.value = false;
  }
};

// Functie om te verwijderen
const handleDelete = async () => {
  if (!confirm("Weet je zeker dat je dit onderwerp wilt verwijderen? Dit kan niet ongedaan worden gemaakt.")) {
    return;
  }

  try {
    if (topic.value && store.token) {
      await deleteTopic(topic.value.id, store.token);
      alert("Onderwerp verwijderd.");
      router.push('/forum');
    }
  } catch (e: any) {
    alert("Kon het onderwerp niet verwijderen: " + e.message);
  }
};

const handleAddComment = async () => {
  if (!topic.value || !store.token) return;
  if (newCommentContent.value.trim().length === 0) {
    commentError.value = "Je reactie mag niet leeg zijn.";
    return;
  }

  commentSubmitting.value = true;
  commentError.value = '';

  let targetParentId: number | undefined = undefined;
  let targetUsername: string | undefined = undefined;

  if (reply.value) {
    targetUsername = reply.value.authorName;
    targetParentId = reply.value.parentId ? reply.value.parentId : reply.value.id;
  }

  try {
    const newComment = await addComment({
      topicId: topic.value.id,
      content: newCommentContent.value,
      parentId: targetParentId,
      replyToUsername: targetUsername
    }, store.token);

    comments.value.push(newComment);
    if (newComment.parentId) expandedComments.value.add(newComment.parentId);

    newCommentContent.value = '';
    reply.value = null;
  } catch (e) {
    commentError.value = "Er ging iets mis bij het plaatsen.";
  } finally {
    commentSubmitting.value = false;
  }
};
</script>

<template>
  <div class="detail-container">
    <router-link to="/forum" class="back-link">← Terug naar overzicht</router-link>

    <div v-if="loading" class="loading">Laden...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else-if="topic" class="topic-full">
      <header class="topic-header">
        <div class="header-top">
          <h1>{{ topic.title }}</h1>
          <div class="action-buttons">
            <button class="btn-like" :disabled="likeBusy" @click="handleLikeToggle" :class="{ 'liked': likedByMe }">
              <span class="heart">{{ likedByMe ? '♥' : '♡' }}</span>
              <span class="count">{{ likeCount }}</span>
            </button>

            <button
              v-if="store.isLoggedIn && topic.authorName === store.username"
              @click="handleDelete"
              class="btn-delete"
              title="Verwijder dit topic"
            >
              🗑️
            </button>

            <button v-if="store.isLoggedIn" @click="handleReport" class="btn-report">⚠</button>
          </div>
        </div>
        <div class="meta">
          Gestart door
          <router-link :to="`/profile/${topic.authorName}`" class="author-link">
            {{ topic.authorName }}
          </router-link>
          op {{ formatDate(topic.createdAt) }}
        </div>
      </header>

      <div class="topic-body">{{ topic.content }}</div>
      <hr class="divider">

      <div class="comments-section">
        <h3>Reacties ({{ comments.length }})</h3>

        <div v-if="comments.length === 0" class="no-comments">
          Er zijn nog geen reacties. Wees de eerste!
        </div>

        <div class="comments-list">

          <div v-for="comment in getRootComments()" :key="comment.id" class="comment-wrapper">

            <article class="comment-item">
              <div class="comment-meta">
                <router-link :to="`/profile/${comment.authorName}`" class="author-link">
                  {{ comment.authorName }}
                </router-link>
                <span class="date">{{ formatDate(comment.createdAt) }}</span>
              </div>

              <div v-if="editingCommentId !== comment.id">
                <div class="comment-content">{{ comment.content }}</div>

                <div class="comment-actions">
                  <button @click="setReply(comment)" class="btn-reply">Reageer</button>
                  <button v-if="store.isLoggedIn" @click="handleReportComment(comment)" class="btn-report">⚠</button>

                  <span v-if="store.username === comment.authorName" class="owner-actions">
                    <span class="dot">•</span>
                    <button @click="startEdit(comment)" class="btn-action edit">Bewerk</button>
                    <span class="dot">•</span>
                    <button @click="handleDeleteComment(comment.id)" class="btn-action delete">Verwijder</button>
                  </span>
                </div>
              </div>

              <div v-else class="edit-box">
                <textarea v-model="editContent" rows="3"></textarea>
                <div class="edit-buttons">
                  <button @click="handleUpdateComment(comment.id)" class="btn-submit small">Opslaan</button>
                  <button @click="cancelEdit" class="btn-cancel">Annuleren</button>
                </div>
              </div>
            </article>

            <div v-if="getReplies(comment.id).length > 0" class="view-replies-wrapper">
              <div class="reply-line"></div>
              <button @click="toggleReplies(comment.id)" class="btn-view-replies">
                <span v-if="expandedComments.has(comment.id)">Verberg antwoorden</span>
                <span v-else>Bekijk {{ getReplies(comment.id).length }} antwoorden</span>
              </button>
            </div>

            <div v-if="expandedComments.has(comment.id)" class="replies-container">
              <div v-for="replyItem in getReplies(comment.id)" :key="replyItem.id" class="reply-wrapper">

                <article class="comment-item reply-item">
                  <div class="comment-meta">
                    <router-link :to="`/profile/${replyItem.authorName}`" class="author-link">
                      {{ replyItem.authorName }}
                    </router-link>
                    <span class="date">{{ formatDate(replyItem.createdAt) }}</span>
                  </div>

                  <div v-if="editingCommentId !== replyItem.id">
                    <div class="comment-content">
                      <span v-if="replyItem.replyToUsername" class="reply-tag">@{{ replyItem.replyToUsername }}</span>
                      {{ replyItem.content }}
                    </div>

                    <div class="comment-actions">
                      <button @click="setReply(replyItem)" class="btn-reply">Reageer</button>
                      <button v-if="store.isLoggedIn" @click="handleReportComment(replyItem)" class="btn-report">⚠</button>

                      <span v-if="store.username === replyItem.authorName" class="owner-actions">
                        <span class="dot">•</span>
                        <button @click="startEdit(replyItem)" class="btn-action edit">Bewerk</button>
                        <span class="dot">•</span>
                        <button @click="handleDeleteComment(replyItem.id)" class="btn-action delete">Verwijder</button>
                      </span>
                    </div>
                  </div>

                  <div v-else class="edit-box">
                    <textarea v-model="editContent" rows="2"></textarea>
                    <div class="edit-buttons">
                      <button @click="handleUpdateComment(replyItem.id)" class="btn-submit small">Opslaan</button>
                      <button @click="cancelEdit" class="btn-cancel">Annuleren</button>
                    </div>
                  </div>
                </article>

              </div>
            </div>

            <div v-if="store.isLoggedIn && reply && reply.id === comment.id" class="inline-reply-box">
              <div class="reply-header">
                ↳ Reactie op <strong>{{ comment.authorName }}</strong>
                <button @click="cancelReply" class="btn-cancel">Annuleren</button>
              </div>
              <textarea v-model="newCommentContent" rows="3" placeholder="Schrijf je antwoord..." :disabled="commentSubmitting"></textarea>
              <div v-if="commentError" class="error-msg">{{ commentError }}</div>
              <button @click="handleAddComment" :disabled="commentSubmitting" class="btn-submit small">
                Plaats Antwoord
              </button>
            </div>

          </div>
        </div>

        <div v-if="store.isLoggedIn && !reply" class="add-comment-form">
          <h4>Plaats een nieuwe reactie</h4>
          <textarea v-model="newCommentContent" placeholder="Wat vind jij hiervan?" rows="3" :disabled="commentSubmitting"></textarea>
          <div v-if="commentError" class="error-msg">{{ commentError }}</div>
          <button @click="handleAddComment" :disabled="commentSubmitting || newCommentContent.trim().length === 0" class="btn-submit">
            {{ commentSubmitting ? 'Plaatsen...' : 'Plaats Reactie' }}
          </button>
        </div>

        <div v-else-if="!store.isLoggedIn" class="login-hint-box">
          <p><router-link to="/login">Log in</router-link> om mee te discussiëren.</p>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-container { max-width: 800px; margin: 0 auto; padding: 20px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #1f2937; }
.back-link { display: inline-block; margin-bottom: 1rem; color: #2563eb; text-decoration: none; font-weight: 500; }
.back-link:hover { text-decoration: underline; }
.loading, .error { text-align: center; margin-top: 2rem; color: #6b7280; }
.error { color: #dc2626; }

.topic-header h1 { margin: 0 0 0.5rem 0; font-size: 2rem; color: #111827; }
.header-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.action-buttons { display: flex; gap: 0.5rem; }
.btn-like { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; border-radius: 20px; border: 1px solid #e5e7eb; background: white; cursor: pointer; font-weight: 600; color: #4b5563; transition: all 0.2s; }
.btn-like:hover { background: #f9fafb; }
.btn-like.liked { background: #fee2e2; border-color: #fecaca; color: #ef4444; }
.btn-like .heart { font-size: 1.2rem; line-height: 1; }
.btn-report { background: none; border: none; cursor: pointer; font-size: 1.2rem; color: #f59e0b; opacity: 0.6; transition: opacity 0.2s; margin-left: 6px; }
.btn-report:hover { opacity: 1; }

.meta { font-size: 0.9rem; color: #6b7280; margin-bottom: 1rem; }
.author-link { color: #2563eb; text-decoration: none; }
.author-link:hover { text-decoration: underline; }

.topic-body { font-size: 1rem; line-height: 1.6; margin-bottom: 2rem; white-space: pre-wrap; }
.divider { border: 0; border-top: 1px solid #e5e7eb; margin: 2rem 0; }

.comments-section h3 { margin-bottom: 1rem; font-size: 1.25rem; color: #111827; }
.no-comments { font-style: italic; color: #6b7280; margin-bottom: 1rem; }
.comments-list { display: flex; flex-direction: column; gap: 1rem; }

.comment-item { background: #fff; padding: 1rem; border-radius: 8px; border: 1px solid #e5e7eb; box-shadow: 0 1px 2px rgba(0,0,0,0.05); position: relative; }
.reply-item { background: #f9fafb; border-left: 4px solid #cbd5e1; margin-left: 20px; }
.comment-meta { display: flex; justify-content: space-between; font-size: 0.85rem; color: #6b7280; margin-bottom: 0.5rem; }
.comment-meta strong { font-weight: 600; color: #111827; }
.comment-content { font-size: 0.95rem; line-height: 1.5; white-space: pre-wrap; }
.reply-tag { background-color: #e0e7ff; color: #1e3a8a; padding: 2px 6px; border-radius: 4px; margin-right: 6px; font-weight: 600; font-size: 0.85rem; }

/* Comment Actions (New Styles Added) */
.comment-actions { margin-top: 0.5rem; display: flex; gap: 8px; align-items: center; }
.owner-actions { display: flex; align-items: center; gap: 8px; }
.dot { color: #ccc; font-size: 0.8rem; }

.btn-reply { background: none; border: none; color: #2563eb; font-weight: 600; cursor: pointer; padding: 0; font-size: 0.85rem; }
.btn-reply:hover { text-decoration: underline; }

.btn-action { background: none; border: none; color: #6b7280; font-weight: 600; cursor: pointer; padding: 0; font-size: 0.85rem; }
.btn-action:hover { color: #111827; text-decoration: underline; }
.btn-action.delete:hover { color: #dc2626; }

/* Edit Box */
.edit-box textarea { width: 100%; margin-bottom: 8px; border: 1px solid #2563eb; background: #fff; }
.edit-buttons { display: flex; gap: 10px; align-items: center; }

/* Replies Toggle */
.view-replies-wrapper { display: flex; align-items: center; margin-left: 20px; margin-top: 5px; gap: 6px; }
.reply-line { width: 40px; height: 2px; background-color: #e5e7eb; }
.btn-view-replies { background: none; border: none; color: #6b7280; font-size: 0.85rem; font-weight: 600; cursor: pointer; }
.btn-view-replies:hover { color: #111827; }

.replies-container { display: flex; flex-direction: column; gap: 0.8rem; margin-top: 0.5rem; margin-left: 20px; }

/* Forms */
.inline-reply-box { background: #f8fafc; border-left: 4px solid #2563eb; padding: 1rem; border-radius: 8px; margin-top: 0.5rem; }
.reply-header { display: flex; justify-content: space-between; margin-bottom: 0.5rem; font-size: 0.85rem; color: #4b5563; }
textarea { width: 100%; padding: 8px; border-radius: 6px; border: 1px solid #d1d5db; resize: vertical; font-family: inherit; margin-bottom: 0.5rem; }
textarea:focus { border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,0.1); }

.btn-submit { background: #2563eb; color: white; border: none; border-radius: 6px; padding: 8px 16px; font-weight: 600; cursor: pointer; }
.btn-submit:hover { background: #1d4ed8; }
.btn-submit:disabled { background: #9ca3af; cursor: not-allowed; }
.btn-submit.small { padding: 6px 12px; font-size: 0.85rem; }

.btn-cancel { background: none; border: none; color: #dc2626; font-weight: 600; cursor: pointer; }
.btn-cancel:hover { text-decoration: underline; }

.login-hint-box { background: #f3f4f6; padding: 1rem; border-radius: 8px; text-align: center; color: #4b5563; }
.login-hint-box a { color: #2563eb; font-weight: 600; }
.login-hint-box a:hover { text-decoration: underline; }
</style>
