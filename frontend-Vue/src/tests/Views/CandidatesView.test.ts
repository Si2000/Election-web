import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import CandidatesView from '@/views/CandidatesView.vue'
import { getAllCandidates } from '@/services/MunicipalityService'

vi.mock('../../services/MunicipalityService', () => ({
  getAllCandidates: vi.fn()
}))

vi.mock('../../components/CandidatesPage.vue', () => ({
  default: { template: '<div class="mock-candidate-list" />' }
}))


describe('CandidatesView', () => {
  const mockData = [
    { dbId: 1, firstname: 'John', initials: 'D.', lastname: 'Doe', gender: 'male', party: 'PartyA', locality: 'Amsterdam' },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    ; // @ts-expect-error
    (getAllCandidates as unknown as vi.Mock).mockResolvedValue(mockData)
  })

  it('laadt kandidaten bij mount', async () => {
    const wrapper = mount(CandidatesView)
    await wrapper.vm.$nextTick()
    await wrapper.vm.$nextTick()

    expect(getAllCandidates).toHaveBeenCalledWith(2023)
    expect(wrapper.find('.mock-candidate-list').exists()).toBe(true)
  })
})
