import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import Candidates from '../../components/CandidatesPage.vue'

describe('Candidates component', () => {
  // Arrange
  const mockCandidates = [
    {
      dbId: 1,
      firstname: 'John',
      initials: 'D.',
      lastname: 'Doe',
      gender: 'male',
      party: 'PartyA',
      locality: 'Amsterdam'
    },
    {
      dbId: 2,
      firstname: 'Jane',
      initials: 'M.',
      lastname: 'Smith',
      gender: 'female',
      party: 'PartyB',
      locality: 'Rotterdam'
    },
    {
      dbId: 3,
      firstname: 'Alex',
      initials: 'X.',
      lastname: 'Taylor',
      gender: 'NULL',
      party: 'PartyC',
      locality: 'Utrecht'
    }
  ]

  it('rendered the correct number of candidate cards', () => {
    // Act
    const wrapper = mount(Candidates, { props: { candidates: mockCandidates } })
    const cards = wrapper.findAll('.candidate-card')
    // Assert
    expect(cards.length).toBe(mockCandidates.length)
  })

  it('renders candidate names correctly', () => {
    // Act
    const wrapper = mount(Candidates, { props: { candidates: mockCandidates } })
    // Assert
    mockCandidates.forEach(candidate => {
      expect(wrapper.text()).toContain(`${candidate.firstname} ${candidate.initials} ${candidate.lastname}`)
    })
  })

  it('renders gender labels correctly', () => {
    // Act
    const wrapper = mount(Candidates, { props: { candidates: mockCandidates } })
    // Assert
    const maleLabel = wrapper.find('.gender-m')
    const femaleLabel = wrapper.find('.gender-v')

    expect(maleLabel.exists()).toBe(true)
    expect(maleLabel.text()).toBe('male')
    expect(femaleLabel.exists()).toBe(true)
    expect(femaleLabel.text()).toBe('female')

    const nullCandidateLabel = wrapper.findAll('.candidate-card')[2].find('.gender-m, .gender-v')
    expect(nullCandidateLabel.exists()).toBe(false)
  })

  it('renders party and locality labels', () => {
    // Act
    const wrapper = mount(Candidates, { props: { candidates: mockCandidates } })
    // Assert
    mockCandidates.forEach((candidate, index) => {
      const card = wrapper.findAll('.candidate-card')[index]
      expect(card.text()).toContain(candidate.party)
      expect(card.text()).toContain(candidate.locality)
    })
  })

  it('shows fallback message if no candidates', () => {
    // Act
    const wrapper = mount(Candidates, { props: { candidates: [] } })
    // Assert
    expect(wrapper.text()).toContain('Geen kandidaten gevonden.')
  })
})
