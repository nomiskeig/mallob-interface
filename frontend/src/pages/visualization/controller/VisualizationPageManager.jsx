import React from 'react';
import { JobContext } from '../../../context/JobContextProvider';
import { SettingsContext } from '../../../context/SettingsContextProvider';
import { UserContext } from '../../../context/UserContextProvider';
import { AppError } from '../../../global/errorHandler/AppError';
import { StreamEventManager } from './StreamEventManager';
import { JobStorage } from '../model/JobStorage';
import { TimeManager } from './TimeManager';
import { Event } from './Event';
import { Visualization } from '../view/Visualization';
import './VisualizationPageManager.scss';
import {JobTreeVertex} from '../model/JobTreeVertex'
import {Job} from '../model/Job'
import {VisTests} from '../view/VisTests'

export class VisualizationPageManager extends React.Component {
	#timeManager;
	#eventManager;
	#jobStorage;
	#context;
	#visRef;
	#visualization;
	constructor(props) {
		super(props);
		this.#timeManager = new TimeManager();
		this.#eventManager = new StreamEventManager(this.#timeManager);
		this.#context = props.context;
		this.#jobStorage = new JobStorage(this.#context);
		this.#visRef = React.createRef();
	}
	shouldComponentUpdate(nextProps) {
		this.#context = nextProps.context;

		this.#jobStorage.updateContext(nextProps.context);
		if (this.#context.userContext.user.isLoaded) {
			this.update();
		}
		return true;
	}
	componentDidMount() {
		console.log('vpm mounted');
		console.log('context after vpm is mounted: \n');
		console.log(this.#context);
		this.#visualization = new Visualization(
			this.#visRef,
			this.update,
			this.#context.settingsContext.settings.amountProcesses,
			this.#jobStorage
		);
        this.#jobStorage.addJobUpdateListener(this.#visualization);
		let initialEvents = this.#eventManager.getSystemState(
			this.#context.userContext
		);
		this.#jobStorage.addEvents(initialEvents);
        
	}
	update() {
		let newEvents = this.#eventManager.getNewEvents(this.#context.userContext);
		this.#jobStorage.addEvents(newEvents);
	}



	render() {
		return (
			<div className='container-fluid'>
				<div className='row'>
					<div className='col-12 col-md-6 visualizationHalf'>
						<div className='visCanvasContainer'>
							<div
								className='visualizationCanvas'
								ref={(el) => (this.#visRef = el)}
							></div>
						</div>
					</div>
					<div className='col-12 col-md-6 binaryTreeHalf'>
						<div className='binaryTreeCanvas'>
                            <VisTests jobStorage={this.#jobStorage}></VisTests>
						</div>
					</div>
				</div>
			</div>
		);
	}
}
