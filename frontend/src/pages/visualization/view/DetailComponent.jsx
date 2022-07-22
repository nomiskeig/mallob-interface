import React from 'react';
import './DetailsComponent.scss'
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
	}
	update() {
		this.forceUpdate();
	}

	render() {
		let job = this.#jobStorage.getJob(this.#jobID);
		let clickedVertex = job ? job.getVertex(this.#clickedTreeIndex) : null;
		function getTitle() {
			if (!clickedVertex) {
				return 'idle rank';
			}
			if (job.getJobName()) {
				return job.getJobName();
			}
			return 'anonymous job';
		}
         
		let title = getTitle();
		return (
			<div className='detailsContainer d-flex align-items-start flex-column'>
				<div className='headerContainer d-flex flex-row'>
					<div className='jobName'>{title}</div>
					<div className='buttons'></div>
				</div>
				{job && clickedVertex && (
					<div className='body d-flex flex-row align-items-start justify-content-center'>
						<div className='details d-flex justify-content-start align-items-start flex-column'>
							<p>
								Rank: {clickedVertex.getRank()}:{' '}
							</p>
							<p>Volume: {job.getSize()}</p>
							<p>
								Subtree Volume:{' '}
								{job.getSubtree(this.#clickedTreeIndex).getSize()}
							</p>
						</div>
						<div className='userInfos}'></div>
					</div>
				)}
			</div>
		);
	}
}
