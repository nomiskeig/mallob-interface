import React from 'react';
import Slider from '@mui/material/Slider';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import format from 'date-fns/format';
import addMilliseconds from 'date-fns/addMilliseconds';
import parseISO from 'date-fns/parseISO';
export class TimelineComponent extends React.Component {
	#timeManager;
	#position;
	#changing = false;
	constructor(props) {
		super(props);

		this.#timeManager = props.timeManager;
	}
	update() {
		this.forceUpdate();
	}

	render() {
		function valueText(number) {
			let text = format(
				addMilliseconds(startTime, number),
				"yyyy-MM-dd', 'HH:mm:ss.SSSxxx"
			);
			return text;
		}
		let startTime = parseISO(this.props.startTime);
		let currentTime = this.#timeManager.isLive()
			? this.#timeManager.getNextTime()
			: new Date();
		let timeDif = differenceInMilliseconds(currentTime, startTime);
		if (!this.#changing) {
			this.#position = differenceInMilliseconds(
				this.#timeManager.getNextTime(),
				startTime
			);
		}

		return (
			<div className='container'>
				<Slider
					step={0.001}
					value={this.#position}
					min={0}
					max={timeDif}
					track={false}
					valueLabelFormat={valueText}
					valueLabelDisplay='auto'
					onChange={(event, newValue) => {
						this.#position = newValue;
						this.#changing = true;
					}}
					onChangeCommitted={(event, newValue) => {
						this.#changing = false;
						this.#timeManager.setNextTime(addMilliseconds(startTime, newValue));
						this.#timeManager.setJump();
					}}
				></Slider>
				<div className='speedContainer row'>
					<button
						className='startButton col'
						onClick={() => this.#timeManager.setPaused(false)}
                            disabled={!this.#timeManager.isPaused()}
					>
						<svg
							xmlns='http://www.w3.org/2000/svg'
							width='20'
							height='20'
							fill='currentColor'
							className='bi bi-play-btn-fill'
							viewBox='0 0 16 16'
						>
							<path d='M0 12V4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm6.79-6.907A.5.5 0 0 0 6 5.5v5a.5.5 0 0 0 .79.407l3.5-2.5a.5.5 0 0 0 0-.814l-3.5-2.5z' />
						</svg>
					</button>
					<button
						className='pauseButton col'
						onClick={() => this.#timeManager.setPaused(true)}
                            disabled={this.#timeManager.isPaused()}
					>
						<svg
							xmlns='http://www.w3.org/2000/svg'
							width='20'
							height='20'
							fill='currentColor'
							className='bi bi-pause-btn-fill'
							viewBox='0 0 16 16'
						>
							<path d='M0 12V4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm6.25-7C5.56 5 5 5.56 5 6.25v3.5a1.25 1.25 0 1 0 2.5 0v-3.5C7.5 5.56 6.94 5 6.25 5zm3.5 0c-.69 0-1.25.56-1.25 1.25v3.5a1.25 1.25 0 1 0 2.5 0v-3.5C11 5.56 10.44 5 9.75 5z' />
						</svg>
					</button>
					<form className='replaySpeedForm col'>
						<label htmlFor='replaySpeed'>Replay speed</label>
						<input
							id='replaySpeed'
							max={200}
							min={-200}
							type='number'
							onChange={(e) => this.#timeManager.setMultiplier(e.target.value)}
							value={this.#timeManager.getMultiplier()}
						></input>
					</form>
				</div>
			</div>
		);
	}
}
