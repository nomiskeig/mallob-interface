import React from 'react';

export class DetailsComponent extends React.Component {
	#jobStorage;
	#clickedTreeIndex;
	#jobID;

	constructor(props) {
		super(props);
		this.#jobStorage = props.jobStorage;
	}

	setClicked(jobID, clickedTreeIndex) {
		this.#jobID = jobID;
		this.#clickedTreeIndex = clickedTreeIndex;
		console.log('clicked');
	}
	update() {
		this.forceUpdate();
	}

	render() {
		let job = this.#jobStorage.getJob(this.#jobID);
		if (!job) {
			return null;
		}
		let clickedVertex = job.getVertex(this.#clickedTreeIndex);
		if (!clickedVertex) {
			return null;
		}
		return (
			<div className='detailsContainer'>
				<div className='headerContainer'>
					<div className='jobName'></div>
					<div className='buttons'></div>
				</div>
				<div className='body'>
					<div className='details'>
						<p>
							Rank: {clickedVertex.getRank()}:{' '}
							<b>
								Job {job.getJobID()}#{clickedVertex.getTreeIndex()}
							</b>
						</p>
						<p>Volume: {job.getSize()}</p>
						<p>
							Subtree Volume: {job.getSubtree(this.#clickedTreeIndex).getSize()}
						</p>
					</div>
					<div className='userInfos'></div>
				</div>
			</div>
		);
	}
}
