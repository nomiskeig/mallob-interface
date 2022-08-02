import { Job } from './Job';
import { JobTreeVertex } from './JobTreeVertex';

test('correctly adds a vertex', () => {
	let job = new Job();
	let vertex = new JobTreeVertex(2, 1);
	job.addVertex(vertex);
	expect(job.getVertex(vertex.getTreeIndex())).toBe(vertex);
});

test('initially has no vertices', () => {
	let job = new Job();
	expect(job.getVertex(1)).toBeNull();
});

test('initially has size 0', () => {
	let job = new Job();
	expect(job.getSize()).toBe(0);
});

test('has size one after adding one vertex', () => {
	let job = new Job();
	let vertex = new JobTreeVertex(2, 1);
	job.addVertex(vertex);
	expect(job.getSize()).toBe(1);
});

test('has correct size after removing a vertex', () => {
	let job = new Job();
	let vertex = new JobTreeVertex(2, 100);
	job.addVertex(vertex);
	expect(job.getSize()).toBe(1);
	job.removeVertex(vertex.getTreeIndex());
	expect(job.getSize()).toBe(0);
});

test('has correct size after adding multiple vertices with non-subsequent indices', () => {
	let job = new Job();
	let vertex1 = new JobTreeVertex(1, 1);
	let vertex2 = new JobTreeVertex(1, 2);
	let vertex3 = new JobTreeVertex(1, 4);
	job.addVertex(vertex1);
	job.addVertex(vertex2);
	expect(job.getSize()).toBe(2);
	job.addVertex(vertex3);
	expect(job.getSize()).toBe(3);
});

test('can remove a vertex', () => {
	let job = new Job();
	let vertex = new JobTreeVertex(2, 1);
	job.addVertex(vertex);
	job.removeVertex(vertex.getTreeIndex());
	expect(job.getVertex(vertex.getTreeIndex())).toBeNull();
});

test('gets parent correctly 1', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 1);
	let parent = new JobTreeVertex(1, 0);
	job.addVertex(child);
	job.addVertex(parent);
	let foundParent = job.getParent(child.getTreeIndex());
	expect(foundParent).toBe(parent);
});

test('gets parent correctly 2', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 2);
	let parent = new JobTreeVertex(1, 0);
	job.addVertex(child);
	job.addVertex(parent);
	let foundParent = job.getParent(child.getTreeIndex());
	expect(foundParent).toBe(parent);
});
test('gets parent correctly 3', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 4);
	let parent = new JobTreeVertex(1, 1);
	job.addVertex(child);
	job.addVertex(parent);
	let foundParent = job.getParent(child.getTreeIndex());
	expect(foundParent).toBe(parent);
});
test('gets left child correctly 1', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 1);
	let parent = new JobTreeVertex(1, 0);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getLeftChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});

test('gets left child  correctly 2', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 5);
	let parent = new JobTreeVertex(1, 2);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getLeftChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});
test('gets left child correctly 3', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 7);
	let parent = new JobTreeVertex(1, 3);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getLeftChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});
test('gets right child correctly 1', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 2);
	let parent = new JobTreeVertex(1, 0);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getRightChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});

test('gets right child  correctly 2', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 6);
	let parent = new JobTreeVertex(1, 2);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getRightChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});
test('gets right child correctly 3', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 8);
	let parent = new JobTreeVertex(1, 3);
	job.addVertex(child);
	job.addVertex(parent);
	let foundChild = job.getRightChild(parent.getTreeIndex());
	expect(foundChild).toBe(child);
});

test('parent is null if not in job', () => {
	let job = new Job();
	let child = new JobTreeVertex(44, 234);
	job.addVertex(child);
	expect(job.getParent(child.getTreeIndex())).toBeNull();
});

test('returns subtree with parent', () => {
	let job = new Job();
	let child = new JobTreeVertex(1, 1);
	let parent = new JobTreeVertex(1, 0);

	job.addVertex(child);
	job.addVertex(parent);

	let subtree = job.getSubtree(child.getTreeIndex());
	expect(subtree.getVertex(child.getTreeIndex())).toBe(child);
	expect(subtree.getVertex(parent.getTreeIndex())).toBe(parent);
	expect(subtree.getSize()).toBe(2);
});

test('returns bigger subtree', () => {
	let job = new Job();
	let vertices = new Array();
	for (let i = 0; i < 8; i++) {
		vertices.push(new JobTreeVertex(0, i));
	}
	vertices.forEach((e) => job.addVertex(e));
	let subtree1 = job.getSubtree(vertices[0].getTreeIndex());
	expect(subtree1.getSize()).toBe(8);
	vertices.forEach((e, i) => {
		expect(subtree1.getVertex(i)).toBe(vertices[i]);
	});
});

test('returns correct subtree when subtree has is not connected', () => {
	let job = new Job();
	let vertex1 = new JobTreeVertex(0, 3);
	let vertex2 = new JobTreeVertex(0, 0);
	let vertex3 = new JobTreeVertex(0, 15);
	job.addVertex(vertex1);
	job.addVertex(vertex2);
	job.addVertex(vertex3);
	let subtree = job.getSubtree(vertex1.getTreeIndex());
	expect(subtree.getSize()).toBe(3);
	expect(subtree.getVertex(vertex1.getTreeIndex())).toBe(vertex1);
	expect(subtree.getVertex(vertex2.getTreeIndex())).toBe(vertex2);
	expect(subtree.getVertex(vertex3.getTreeIndex())).toBe(vertex3);
});

test('returns null if the child is is not part of the job', () => {
	let job = new Job();
	let vertex = new JobTreeVertex(0, 3);
	job.addVertex(vertex);
	expect(job.getLeftChild(vertex.getTreeIndex())).toBeNull();
	expect(job.getRightChild(vertex.getTreeIndex())).toBeNull();
});

test('return the vertices', () => {
	let job = new Job();
	let vertex1 = new JobTreeVertex(0, 3);
	job.addVertex(vertex1);
	expect(job.getVertices()).toEqual([vertex1]);
	let vertex2 = new JobTreeVertex(1, 4);
	job.addVertex(vertex2);
	expect(job.getVertices().sort()).toEqual([vertex1, vertex2].sort());
});

test('sets the initial color', () => {
	let color = '#123456';
	let job = new Job(1, color);
	expect(job.getColor()).toBe(color);
});

test('set the color with the setter', () => {
	let color1 = '#123456';
	let color2 = '#654321';
	let job = new Job(1, color1);
	job.setColor(color2);
	expect(job.getColor()).toBe(color2);
});

test('sets the username', () => {
	let username = 'bob';
	let job = new Job(1, '#123456');
	job.setUsername(username);
	expect(job.getUsername()).toBe(username);
});

test('sets the useremail', () => {
	let email = 'mail1';
	let job = new Job(1, '#123456');
	job.setUserEmail(email);
	expect(job.getUserEmail()).toBe(email);
});

test('sets the initial jobID', () => {
	let job = new Job(1, '#123456');
	expect(job.getJobID()).toBe(1);
});

test('sets the outer color', () => {
	let outerColor = '#654321';
	let job = new Job(1, '#123456');
	job.setOuterColor(outerColor);
	expect(job.getOuterColor()).toBe(outerColor);
});

test('sets the job name', () => {
	let jobname = 'job1name';
	let job = new Job(1, '#123456');
	job.setJobName(jobname);
	expect(job.getJobName()).toBe(jobname);
});

test('overwrites a vertex if theres already a vertex with on that treeIndex', () => {
	let job = new Job(1, '#123456');
	let vertex1 = new JobTreeVertex(0, 2);
	let vertex2 = new JobTreeVertex(1, 2);
	job.addVertex(vertex1);
	expect(job.getSize()).toBe(1);
	expect(job.getVertex(vertex1.getTreeIndex())).toBe(vertex1);
	job.addVertex(vertex2);
	expect(job.getSize()).toBe(1);
	expect(job.getVertex(vertex2.getTreeIndex())).toBe(vertex2);
	expect(job.getVertex(vertex2.getTreeIndex())).toBe(vertex2);
});

test('removing a non existent vertex does not break', () => {
	let job = new Job(1, '#123456');
	job.removeVertex(1);
	expect(job.getSize()).toBe(0);
});

test('getVertices returns empty array if there is no vertex', () =>{
    let job = new Job(1, '#123456');
    expect(job.getVertices()).toEqual([]);
})
