import React from 'react';
import { StreamEventManager } from './StreamEventManager';
import { PastEventManager } from './PastEventManager';
import { JobStorage } from '../model/JobStorage';
import { TimeManager } from './TimeManager';
import { Visualization } from '../view/Visualization';
import './VisualizationPageManager.scss';
import { TimelineComponent } from '../view/TimelineComponent';
import { GlobalStatsComponent } from '../view/GlobalStatsComponent';
import { DetailsComponent } from '../view/DetailsComponent';
import { Event } from './Event';
import { BinaryTree } from '../view/BinaryTree';
export class VisualizationPageManager extends React.Component {
	#timeManager;
	#eventManager;
	#jobStorage;
	#context;
	#visualizationRef;
	#visualization;
	#timeLineComponent;
	#globalStatsComponent;
	#detailsComponent;
	#stateLoaded;
	#shouldUpdate;
	#binaryTree;
	#binaryTreeRef;
	constructor(props) {
		super(props);
		this.#timeManager = new TimeManager();
		this.#eventManager = new StreamEventManager(this.#timeManager);
		this.#context = props.context;
		this.#jobStorage = new JobStorage(this.#context);
		this.#visualizationRef = React.createRef();
		this.#binaryTreeRef = React.createRef();
		this.#timeLineComponent = React.createRef();
		this.#globalStatsComponent = React.createRef();
		this.#detailsComponent = React.createRef();
		this.#stateLoaded = false;
		this.#shouldUpdate = true;
	}
	shouldComponentUpdate(nextProps) {
		console.log('shouldComponentUpdate');
		this.#context = nextProps.context;

		this.#jobStorage.updateContext(nextProps.context);
		this.#timeLineComponent.updateContext(nextProps.context);
		if (this.#context.userContext.user.isLoaded) {
			this.update();
		}
		return true;
	}

	componentDidMount() {
		this.#context.jobContext.fetchMostJobsPossible();
		this.#visualization = new Visualization(
			this.#visualizationRef,
			this.update.bind(this),
			this.#context.settingsContext.settings.amountProcesses,
			this.#jobStorage,
			this.onClick.bind(this)
		);
		this.#binaryTree = new BinaryTree(
			this.#jobStorage,
			this.#binaryTreeRef,
			this.#context.settingsContext.settings.amountProcesses
		);
		this.#jobStorage.addJobUpdateListener(this.#binaryTree);
		this.#jobStorage.addJobUpdateListener(this.#visualization);
		this.#eventManager.getSystemState(this.#context.userContext).then((res) => {
			this.#jobStorage.addEvents(res);
			this.#stateLoaded = true;
		});
		console.log('mounted');
	}

	componentWillUnmount() {
		console.log('will unmount');
		this.#shouldUpdate = false;
		this.#eventManager.closeStream();
		this.#visualization.stop();
		this.#visualization = null;
		this.#eventManager = null;
		this.#jobStorage = null;
	}
	update() {
		console.log('updating');
		if (!this.#shouldUpdate) {
			return;
		}
		try {
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

				this.#eventManager
					.getSystemState(this.#context.userContext)
					.then((res) => {
						this.#jobStorage.addEvents(res);
						this.#stateLoaded = true;
					});
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
		} catch (e) {
			if (this.#shouldUpdate) {
				this.#context.infoContext.handleInformation(
					e.getMessage(),
					e.getType()
				);
			}
		}
	}

	onClick(jobID, treeIndex) {
		this.#detailsComponent.setClicked(jobID, treeIndex);
		if (jobID !== null) {
			this.#binaryTree.displayTree(jobID, treeIndex);
		} else {
			this.#binaryTree.clearTree();
		}
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
								context={this.#context}
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
								context={this.#context}
								jobStorage={this.#jobStorage}
							></DetailsComponent>
							<div className='binaryTreeCanvasContainer'>
								<div
									className='binaryTreeCanvas'
									ref={(el) => (this.#binaryTreeRef = el)}
								></div>
							</div>
							<button
								onClick={() => {
									console.log('showing tree');
									this.#jobStorage.reset();
									this.#timeManager.setPaused(true);
									this.#shouldUpdate = false;
									let events = [];
									for (let i = 0; i < 250; i += 1) {
										events.push(new Event(null, i * 4, i, 4, 1));
									}
									this.#jobStorage.addEvents(events);
								}}
							>
								Show tree
							</button>

							<button
								onClick={() => {
									let events = [];
									for (let i = 8; i < 15; i += 1) {
										events.push(new Event(null, i * 4, i, 4, 0));
									}
									this.#jobStorage.addEvents(events);
								}}
							>
								Remove some vertices from the tree
							</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}
