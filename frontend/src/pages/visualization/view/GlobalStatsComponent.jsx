import React from 'react';
import './GlobalStatsComponent.scss'

export class GlobalStatsComponent extends React.Component {
	#globalStats;
	constructor(props) {
		super(props);
		this.#globalStats = props.globalStats;
	}

	update() {
		this.forceUpdate();
	}

	render() {
		let used = this.#globalStats.getUsedProcesses();
		let total = this.#globalStats.getProcesses();
		let percentage = used / total * 100;
		return (
			<div className='globalStatsContainer d-flex flex-row justify-content-around flex-wrap'>
				<div className='activeJobs'>
					Active Jobs: {this.#globalStats.getActiveJobs()}
				</div>
				<div className='systemLoad'>
					System load: {used}/{total} ({percentage}%)
				</div>
			</div>
		);
	}
}
