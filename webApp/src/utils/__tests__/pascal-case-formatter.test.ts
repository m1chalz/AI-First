import { describe, it, expect } from 'vitest';
import toPascalCase from '../pascal-case-formatter';

describe('toPascalCase', () => {
  it.each<[string, string]>([
    ['hello', 'Hello'],
    ['HELLO', 'Hello'],
    ['HeLLo', 'Hello'],
    ['', ''],
    ['a', 'A'],
    ['A', 'A'],
    ['hello world test', 'Hello world test'],
    ['hello-world', 'Hello-world'],
    ['hello_world', 'Hello_world'],
    ['123hello', '123hello'],
    ['hello123world', 'Hello123world'],
    ['@hello', '@hello'],
    ['   ', '   '],
    ['Hello', 'Hello'],
    ['CAT', 'Cat'],
    ['GERMAN SHEPHERD', 'German shepherd'],
    ['MALE', 'Male'],
    ['UNKNOWN', 'Unknown']
  ])('toPascalCase(%s) should return %s', (input, expected) => {
    // when
    const result = toPascalCase(input);

    // then
    expect(result).toBe(expected);
  });
});
