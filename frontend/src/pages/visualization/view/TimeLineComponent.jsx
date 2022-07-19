import React from 'react';
import Slider from '@mui/material/Slider';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import format from 'date-fns/format';
import addMilliseconds from 'date-fns/addMilliseconds';
import parseISO from 'date-fns/parseISO';
export class TimelineComponent extends React.Component {
	#timeManager;
	constructor(props) {
		super(props);
		this.#timeManager = props.timeManager;
	}
	update() {
		this.forceUpdate();
		//this.#slider += 1;
	}
	render() {
		function valueText(number) {
			//console.log('gettings label '+ number)
			let text = format(
				addMilliseconds(startTime, number),
				"yyyy-MM-dd'T'HH:mm:ss.SSSxxx"
			);
			//console.log(text);
			return text;
		}
		let steps = 20;
		let startTime = parseISO(this.props.startTime);
		let currentTime = this.#timeManager.isLive()
			? this.#timeManager.getLastTime()
			: new Date();
		console.log(currentTime);
		let timeDif = differenceInMilliseconds(currentTime, startTime);
		//console.log(timeDif);
		let distance = timeDif / steps;
		const marks = [];
		for (let i = 0; i < steps; i++) {
			let time = addMilliseconds(startTime, distance * (i + 1));
			//	console.log(distance);
			//	console.log(startTime);
			//	console.log(time);
			marks.push({
				value: i * 5,
				//label: format(time, j"yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
				label: '',
			});
		}

		return (
			<div className='container'>
				<Slider
                    //update={this.#slider}
					step={1}
					min={0}
					max={timeDif}
					track={false}
					valueLabelFormat={valueText}
					valueLabelDisplay='auto'
				></Slider>
				<div className='speedContainer'>
					<input type='text' placeholder='1'></input>
				</div>
			</div>
		);
	}
}
