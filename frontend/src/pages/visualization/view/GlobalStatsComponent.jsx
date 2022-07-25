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
        // round the percentag to mitigate computer arithmetic errors 
        let roundedPercentage =  (Math.round(percentage * 100)/100).toFixed(3);
		return (
			<div className='globalStatsContainer d-flex flex-row justify-content-around flex-wrap'>
				<div className='activeJobs'>
					Active Jobs: {this.#globalStats.getActiveJobs()}
				</div>
				<div className='systemLoad'>
					System load: {used}/{total} ({roundedPercentage}%)
				</div>
			</div>
		);
	}
}
