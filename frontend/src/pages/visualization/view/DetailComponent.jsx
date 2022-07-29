import React from 'react';
import './DetailsComponent.scss';
import { Button } from '../../../global/buttons/Button';
import { JobPageButton } from '../../../global/buttons/JobPageButton';
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
		let showUserInfos = false;
		if (job) {
			if (job.getUsername()) {
				showUserInfos = true;
			}
		}

		return (
			<div className='detailsContainer d-flex align-items-start flex-column'>
				<div className='headerContainer d-flex flex-row align-items-center'>
					<div className='jobName'>{title}</div>
					<div className='buttons ms-auto d-flex flex-row '>
							<React.Fragment>
								<Button color='#f24236' text='cancel job'></Button>
                            <div className='buttonSpacer'></div>
								<JobPageButton className='jobButton'></JobPageButton>
							</React.Fragment>
					</div>
				</div>
				{job && clickedVertex && (
					<div className='bodyContainer d-flex flex-row align-items-start justify-content-between'>
						<div className='details d-flex justify-content-start align-items-start flex-column'>
							<p>Rank: {clickedVertex.getRank()} </p>
							<p>Volume: {job.getSize()}</p>
							<p>
								Subtree Volume:{' '}
								{job.getSubtree(this.#clickedTreeIndex).getSize()}
							</p>
						</div>
						<div className='userInfos d-flex justify-content-start align-items-start flex-column'>
							{showUserInfos && (
								<React.Fragment>
									<p>User information:</p>
									<p>Name: {job.getUsername()}</p>
									<p>Email: {job.getUserEmail()}</p>
								</React.Fragment>
							)}
						</div>
					</div>
				)}
			</div>
		);
	}
}
