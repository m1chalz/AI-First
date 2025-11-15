import { beforeAll, describe, it } from 'vitest'
import assert from 'node:assert'
import request from 'supertest'

import prepareApp from '../app.js'
import type { Express } from 'express'

describe('Pets ITs', () => {
  let app: Express;

  beforeAll(async () => {
    app = await prepareApp();
  })

  it('should return a json body', async () => {
    // Given / when
    const response = await request(app)
      .get('/pets')
      .send()

    // Then
    assert.equal(200, response.status)
    assert.equal(3, response.body.length)
  })

})
