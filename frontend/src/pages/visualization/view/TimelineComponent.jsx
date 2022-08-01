import React from 'react';
import Slider from '@mui/material/Slider';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import format from 'date-fns/format';
import addMilliseconds from 'date-fns/addMilliseconds';
import parseISO from 'date-fns/parseISO';
import './TimelineComponent.scss';
import { TYPE_WARNING } from '../../../context/InfoContextProvider';
export class TimelineComponent extends React.Component {
	#timeManager;
	#position;
	#changing = false;
	#context;
	constructor(props) {
		super(props);
		this.#context = props.context;
		this.#timeManager = props.timeManager;
	}
	update() {
		this.forceUpdate();
	}
	updateContext(context) {
		this.#context = context;
	}

	render() {
		function valueText(number) {
			let text = format(
				addMilliseconds(startTime, number),
				"yyyy-MM-dd', 'HH:mm:ss.SSSxxx"
			);
			return text;
		}
		let startTime = parseISO(this.#context.settingsContext.settings.startTime);
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
			<div className='timelineContainer d-flex flex-column align-items-center'>
				<div className='sliderContainer'>
					<Slider
						step={1}
						value={this.#position}
						min={0}
						max={timeDif}
						track={false}
						valueLabelFormat={valueText}
						valueLabelDisplay='auto'
						onChange={(event, newValue) => {
							console.log(newValue);
							this.#position = newValue;
							this.#changing = true;
						}}
						onChangeCommitted={(event, newValue) => {
							this.#changing = false;
							console.log(newValue);
							this.#timeManager.setNextTime(
								addMilliseconds(startTime, newValue)
							);
							this.#timeManager.setJump();
						}}
						sx={{
							'& .MuiSlider-thumb': {
								transition: 'none',
							},
                            color: '#45b087'
						}}
					></Slider>
				</div>
				<div className='speedContainer d-flex flex-row flex-wrap justify-items-center'>
					<button
						className='controlButton btn btn-primary'
						onClick={() => this.#timeManager.setPaused(false)}
						disabled={!this.#timeManager.isPaused()}
					>
						<svg
							xmlns='http://www.w3.org/2000/svg'
							width='32'
							height='32'
							fill='currentColor'
							className='bi bi-play-fill'
							viewBox='3 3 10 10'
						>
							<path d='m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z' />
						</svg>
					</button>
					<button
						className='controlButton btn btn-primary pauseButton'
						onClick={() => this.#timeManager.setPaused(true)}
						disabled={this.#timeManager.isPaused()}
					>
						<svg
							xmlns='http://www.w3.org/2000/svg'
							width='32'
							height='32'
							fill='currentColor'
							className='bi bi-pause-fill'
							viewBox='3 3 10 10'
						>
							<path d='M5.5 3.5A1.5 1.5 0 0 1 7 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5zm5 0A1.5 1.5 0 0 1 12 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5z' />
						</svg>
					</button>
					<div className='flex-fill'></div>
					<form className='replaySpeedForm d-flex align-items-center'>
						<label className='replaySpeedLabel' htmlFor='replaySpeed'>
							Replay speed
						</label>
						<input
							className='replaySpeedInput form-control'
							id='replaySpeed'
							//max={200}
							//min={-200}
							type='number'
							onChange={(e) => {
								//e.preventDefault()
								if (isNaN(e.target.value)) {
									this.#context.infoContext.handleInformation(
										e.target.value + ' is not a number.',
										TYPE_WARNING
									);
                                    return;
								}
                                if (e.target.value > 200) {
                                    this.#context.infoContext.handleInformation('The biggest possible multiplier is 200.', TYPE_WARNING)
                                    return;
                                }
                                if (e.target.value < 0) {
                                    this.#context.infoContext.handleInformation('Negative multipliers are currently not supported.', TYPE_WARNING)
                                    return;
                                }
								this.#timeManager.setMultiplier(e.target.value);
							}}
							value={this.#timeManager.getMultiplier()}
						></input>
					</form>
				</div>
			</div>
		);
	}
}
