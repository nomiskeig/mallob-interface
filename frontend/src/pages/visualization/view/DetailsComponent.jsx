import React from 'react';
import './DetailsComponent.scss';
import { Button } from '../../../global/buttons/Button';
import { JobPageButton } from '../../../global/buttons/JobPageButton';
/**
 * This class displays details for the job which is is clicked on in the visualization.
 * 
 * @author Simon Giek
 * @extends React.Component
 */
export class DetailsComponent extends React.Component {
	#jobStorage;
	#clickedTreeIndex;
	#jobID;

    /**
     * The constructor.
     *
     * @param {Object} props 
     * @param {JobStorage} props.jobStorage - The instance of JobStorage to use.
     */
	constructor(props) {
		super(props);
		this.#jobStorage = props.jobStorage;
	}

    /**
     * Sets the private attributes jobID and clickedTreeIndex.
     *
     * @param {int} jobID - The ID of the job which was clicked on.
     * @param {int} clickedTreeIndex - The index of the vertex which was clicked on.
     */
	setClicked(jobID, clickedTreeIndex) {
		this.#jobID = jobID;
		this.#clickedTreeIndex = clickedTreeIndex;
	}
    /**
     * Updates the displayed information.
     *
     */
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
        
        let showButtons = false;

		if (job) {
			if (job.getUsername()) {
				showUserInfos = true;
			}
            if (job.getJobName() && clickedVertex) {
                showButtons = true;
            }
		}

		return (
			<div className='detailsContainer d-flex align-items-start flex-column'>
				<div className='headerContainer d-flex flex-row align-items-center'>
					<div className='jobName'>{title}</div>
					<div className='buttons ms-auto d-flex flex-row '>
						{showButtons  && (
							<React.Fragment>
                                {this.props.context.jobContext.jobs.find(Job => Job.jobID == job.getJobID()).status === 'RUNNING' &&
								<Button color='#f24236' text='cancel job' onClick={() => {
                                    this.props.context.jobContext.cancelJob(job.getJobID())
                                }}></Button>
                            }
								<div className='buttonSpacer'></div>
								<JobPageButton className='jobButton' jobID={job.getJobID()}></JobPageButton>
							</React.Fragment>
						)}
					</div>
				</div>
				{job && clickedVertex && (
					<div className='bodyContainer d-flex flex-row align-items-start justify-content-between'>
						<div className='details d-flex justify-content-start align-items-start flex-column'>
							<p>Rank: {clickedVertex.getRank()} </p>
                            <p>Index: {clickedVertex.getTreeIndex()} </p>
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
