<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getConstituency } from '@/services/ElectionService'

interface Candidate {
  name: string
  votes: string
}

interface Metadata {
  [key: string]: string
}

interface Party {
  name: string
  totalVotes: number
  candidates: Candidate[]
}

interface Constituency {
  name: string
  parties: Party[]
  metadata?: Metadata
}

interface Election {
  id: string
  constituencies: Constituency[]
}

const electionYear = ref('TK2023')
const constituencies = ref<Constituency[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const fetchConstituencies = async () => {
  loading.value = true
  error.value = null

  try {
    const data: Election = await getConstituency(electionYear.value)
    console.log('Fetched constituencies:', data)
    constituencies.value = data.constituencies
  } catch (err: any) {
    console.error(err)
    error.value = `Failed to load constituencies for ${electionYear.value}`
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchConstituencies()
})

watch(electionYear, () => {
  fetchConstituencies()
})
</script>

<template>
  <div>
    <h1>Constituencies View</h1>

    <div class="mb-4">
      <label for="year">Verkiezingsjaar:</label>
      <select id="year" v-model="electionYear">
        <option value="TK2021">TK2021</option>
        <option value="TK2023">TK2023</option>
      </select>
    </div>

    <div v-if="loading">Loading constituencies...</div>
    <div v-if="error">{{ error }}</div>

    <ul v-if="!loading && !error">
      <li v-for="c in constituencies" :key="c.name" class="mb-4">
        <strong>{{ c.name }}</strong>

        <ul>
          <li v-for="p in c.parties" :key="p.name">
            {{ p.name }} ({{ p.totalVotes }} votes)
            <ul>
              <li v-for="cand in p.candidates" :key="cand.name">
                {{ cand.name }}: {{ cand.votes }}
              </li>
            </ul>
          </li>
        </ul>

        <div v-if="c.metadata" class="mt-3 p-2 bg-gray-100 rounded">
          <h4 class="font-semibold mb-1">Metadata:</h4>
          <ul>
            <li v-for="(value, key) in c.metadata" :key="key">
              <strong>{{ key }}:</strong> {{ value }}
            </li>
          </ul>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
label {
  margin-right: 8px;
  font-weight: 600;
}
select {
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
</style>
