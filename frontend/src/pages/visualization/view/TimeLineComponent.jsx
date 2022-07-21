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
        console.log('updated')
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
            this.#position = differenceInMilliseconds(this.#timeManager.getNextTime(), startTime);
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
                        console.log('set next time')
					}}
				></Slider>
				<div className='speedContainer'>
					<input type='text' placeholder='1'></input>
				</div>
			</div>
		);
	}
}
