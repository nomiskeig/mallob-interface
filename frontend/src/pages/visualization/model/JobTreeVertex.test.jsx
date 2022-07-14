import { JobTreeVertex } from './JobTreeVertex';
import { AppError } from '../../../global/errorHandler/AppError';

test('calculates the depth correctly', () => {
	let vertex1 = new JobTreeVertex(1, 0);
	expect(vertex1.getDepth()).toBe(0);
	let vertex2 = new JobTreeVertex(1, 1);
	expect(vertex2.getDepth()).toBe(1);
	let vertex3 = new JobTreeVertex(1, 2);
	expect(vertex3.getDepth()).toBe(1);
	let vertex4 = new JobTreeVertex(1, 3);
	expect(vertex4.getDepth()).toBe(2);
	let vertex5 = new JobTreeVertex(1, 6);
	expect(vertex5.getDepth()).toBe(2);
	let vertex6 = new JobTreeVertex(1, 7);
	expect(vertex6.getDepth()).toBe(3);
});

test('returns the correct rank', () => {
	let vertex = new JobTreeVertex(1, 2, 3);
	expect(vertex.getRank()).toBe(1);
});

test('returns the correct treeIndex', () => {
	let vertex = new JobTreeVertex(3, 2);
	expect(vertex.getTreeIndex()).toBe(2);
});

test('does not accept negative Rank', () => {
	expect(() => {
		let vertex = new JobTreeVertex(-2, 1);
	}).toThrow(AppError);
});
test('does not accept negative treeIndex', () => {
	expect(() => {
		let vertex = new JobTreeVertex(2, -1);
	}).toThrow(AppError);
});
