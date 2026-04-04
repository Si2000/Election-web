import { describe, it, expect, vi } from 'vitest'
import { loginUser, registerUser } from '@/services/AuthService'

describe('AuthService', () => {

  it('should login a user', async () => {
    // Arrange
    const mockResponse = { token: 'abc123', user: { email: 'test@test.com' } }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => mockResponse,
    }))

    // Act
    const result = await loginUser('test@test.com', 'password')

    // Assert
    expect(result).toEqual(mockResponse)
  })

  it('should throw an error on wrong login', async () => {
    // Arrange
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 401,
      text: async () => 'Unauthorized'
    }))

    // Act & Assert
    await expect(loginUser('wrong@test.com', 'wrong')).rejects.toThrow(
      'HTTP error! status: 401, body: Unauthorized'
    )
  })

  it('should register a new user', async () => {
    // Arrange
    const mockResponse = { message: 'User registered' }
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      status: 201,
      json: async () => mockResponse,
    }))

    // Act
    const result = await registerUser('new@test.com', 'password', 'newuser')

    // Assert
    expect(result).toEqual(mockResponse)
  })

  it('should throw an error on failed registration', async () => {
    // Arrange
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: false,
      status: 400,
      text: async () => 'Bad Request'
    }))

    // Act & Assert
    await expect(registerUser('bad@test.com', '', '')).rejects.toThrow(
      'HTTP error! status: 400, body: Bad Request'
    )
  })
})
