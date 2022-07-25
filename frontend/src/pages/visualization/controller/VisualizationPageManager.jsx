import React from 'react';
import { JobContext } from '../../../context/JobContextProvider';
import { SettingsContext } from '../../../context/SettingsContextProvider';
import { UserContext } from '../../../context/UserContextProvider';
import { AppError } from '../../../context/AppError';
import { StreamEventManager } from './StreamEventManager';
import {PastEventManager} from './PastEventManager';
import { JobStorage } from '../model/JobStorage';
import { TimeManager } from './TimeManager';
import { Event } from './Event';
import { Visualization } from '../view/Visualization';
import './VisualizationPageManager.scss';
import { JobTreeVertex } from '../model/JobTreeVertex';
import { Job } from '../model/Job';
import { VisTests } from '../view/VisTests';
import { TimelineComponent } from '../view/TimelineComponent';
import { GlobalStatsComponent } from '../view/GlobalStatsComponent';
import { DetailsComponent } from '../view/DetailComponent';
export class VisualizationPageManager extends React.Component {
	#timeManager;
	#eventManager;
	#jobStorage;
	#context;
	#visualizationRef;
	#visualization;
	#test;
	#timeLineComponent;
	#globalStatsComponent;
	#detailsComponent;
	#stateLoaded;
	constructor(props) {
		super(props);
		this.#timeManager = new TimeManager();
		this.#eventManager = new StreamEventManager(this.#timeManager);
		this.#context = props.context;
		this.#jobStorage = new JobStorage(this.#context);
		this.#visualizationRef = React.createRef();
		this.#timeLineComponent = React.createRef();
		this.#globalStatsComponent = React.createRef();
		this.#test = new VisTests({ props: this.#jobStorage });
		this.#detailsComponent = React.createRef();
		this.#stateLoaded = false;
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
		this.#visualization = new Visualization(
			this.#visualizationRef,
			this.update.bind(this),
			this.#context.settingsContext.settings.amountProcesses,
			this.#jobStorage,
			this.onClick.bind(this)
		);
		this.#jobStorage.addJobUpdateListener(this.#visualization);
		this.#eventManager.getSystemState(this.#context.userContext).then((res) => {
			this.#jobStorage.addEvents(res);
			this.#stateLoaded = true;
		});
		console.log('mounted');
        this.#context.infoContext.displayWarning('this is a warning')
		//this.#jobStorage.addEvents(initialEvents);
	}
	update() {
		this.#timeManager.getNextTime();
        // jump is required => reload the system state etc.
        if (this.#timeManager.getJump()) {
            
            this.#stateLoaded = false;
            this.#eventManager.closeStream();
            this.#jobStorage.reset();
            if (this.#timeManager.isLive()) {
                this.#eventManager = new StreamEventManager(this.#timeManager);
            } else {
                this.#eventManager = new PastEventManager(this.#timeManager);
            }
            
            this.#eventManager.getSystemState(this.#context.userContext).then((res) => {
                this.#jobStorage.addEvents(res);
                this.#stateLoaded = true;
            })
            //this.#timeManager.setJump();
        }
		if (this.#stateLoaded) {
			let newEvents = this.#eventManager.getNewEvents(
				this.#context.userContext
			);
			this.#jobStorage.addEvents(newEvents);
		}
		this.#timeLineComponent.update();
		this.#globalStatsComponent.update();
		this.#detailsComponent.update();
		this.#timeManager.updateTime();
	}

	onClick(jobID, treeIndex) {
		this.#detailsComponent.setClicked(jobID, treeIndex);
	}

	render() {
		return (
			<div className='pageContainer'>
				<div className='row g-0'>
					<div className='col-12 col-md-6 visualizationHalf d-flex align-items-center justify-content-center'>
						<div className='halfContainer d-flex flex-column align-items-center'>
							<div className='visCanvasContainer'>
								<div
									className='visualizationCanvas'
									ref={(el) => (this.#visualizationRef = el)}
								></div>
							</div>
							<TimelineComponent
								timeManager={this.#timeManager}
								startTime={this.#context.settingsContext.settings.startTime}
								ref={(el) => (this.#timeLineComponent = el)}
							></TimelineComponent>
							<GlobalStatsComponent
								ref={(el) => (this.#globalStatsComponent = el)}
								globalStats={this.#jobStorage.getGlobalStats()}
							></GlobalStatsComponent>
						</div>
					</div>
					<div className='col-12 col-md-6 binaryTreeHalf d-flex align-items-center justify-content-center'>
						<div className='halfContainer d-flex flex-column align-items-center'>
							<DetailsComponent
								ref={(el) => (this.#detailsComponent = el)}
								jobStorage={this.#jobStorage}
							></DetailsComponent>
							<div className='binaryTreeCanvasContainer'>
								<div className='binaryTreeCanvas'></div>
							</div>
							<VisTests jobStorage={this.#jobStorage}></VisTests>
							<button onClick={() => this.#test.startGiveRandomEvents()}>
								Start events on update
							</button>
							<button onClick={() => this.#test.stopGiveRandomEvents()}>
								Stop events on update
							</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}
