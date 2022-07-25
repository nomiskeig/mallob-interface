import React from 'react';
import { Event } from '../controller/Event';
import { JobTreeVertex } from '../model/JobTreeVertex';
import { devSettings } from '../../../context/devDefaults';

export class VisTests extends React.Component {
	#jobStorage;
	#interval;
	#advancedInterval;
	#used = {};
	#giveEvent = false;
    #usedTreeIndices = {} 
	constructor(props) {
		super(props);
		this.#jobStorage = props.jobStorage;
	}
	testJobs() {
		let event1 = new Event(null, 1, 0, 1, 1);
		let event2 = new Event(null, 10, 1, 1, 1);
		let event3 = new Event(null, 100, 3, 1, 1);
		let event4 = new Event(null, 89, 100, 1, 1);
		let vertex2 = new JobTreeVertex(10, 1);
		let vertex3 = new JobTreeVertex(100, 3);
		let vertex4 = new JobTreeVertex(89, 100);

		let event5 = new Event(null, 0, 4, 2, 1);
		let event6 = new Event(null, 3, 0, 2, 1);
		let event7 = new Event(null, 99, 1, 2, 1);
		let event8 = new Event(null, 98, 3, 2, 1);
		let vertex5 = new JobTreeVertex(0, 4);
		let vertex6 = new JobTreeVertex(3, 0);
		let vertex7 = new JobTreeVertex(99, 1);
		let vertex8 = new JobTreeVertex(98, 3);
		this.#jobStorage.addEvents([
			event1,
			event2,
			event3,
			event4,
			event5,
			event6,
			event7,
			event8,
		]);
	}

	addLongTestJob() {
		let event1 = new Event(null, 101, 0, 0, 1);
		let event2 = new Event(null, 111, 1, 0, 1);
		let event3 = new Event(null, 121, 3, 0, 1);
		let event4 = new Event(null, 131, 7, 0, 1);
		let event5 = new Event(null, 141, 15, 0, 1);
		let event6 = new Event(null, 151, 31, 0, 1);
		let event7 = new Event(null, 161, 63, 0, 1);
		let event8 = new Event(null, 171, 127, 0, 1);
		this.#jobStorage.addEvents([
			event1,
			event2,
			event3,
			event4,
			event5,
			event6,
			event7,
			event8,
		]);
	}
	addFullJob() {
		let processes = devSettings.amountProcesses;
		let events = new Array();
		for (let i = 0; i < processes; i++) {
			events.push(new Event(null, i, i, 4, 1));
		}
		this.#jobStorage.addEvents(events);
	}

	removeFromLongJob() {
		let event1 = new Event(null, 131, 7, 3, 0);
		this.#jobStorage.addEvents([event1]);
	}

	startAddRandomEvents() {
		this.#interval = setInterval(() => {
            
			let newEvent = new Event(
				null,
				Math.floor(Math.random() * 100),
				Math.floor(Math.random() * 100),
				6,
				1
			);

			this.#jobStorage.addEvents([newEvent]);
		});
	}
	getRandomEvent() {
        function generateEvent() {

			let newEvent = new Event(
				null,
				Math.floor(Math.random() * 1000),
				Math.floor(Math.random() * 1000),
				6,
				1
			);
            return newEvent;
        }
		if (this.#giveEvent) {
            let newEvent= generateEvent()
			if (this.#used[newEvent.getRank()] !== undefined) {
                
				newEvent = new Event(
					null,
					newEvent.getRank(),
					this.#used[newEvent.getRank()],
					6,
					0
				);
				delete this.#used[newEvent.getRank()];
                this.#usedTreeIndices[newEvent.getTreeIndex()] = null;
			} else {
                if (this.#usedTreeIndices[newEvent.getTreeIndex()]) {
                    return null;
                }
				this.#used[newEvent.getRank()] = newEvent.getTreeIndex();
                this.#usedTreeIndices[newEvent.getTreeIndex()] = true;
			}
			/*if (newEvent.getLoad() === 1) {
				console.log(
					'loaded rank ' +
						newEvent.getRank() +
						'with treeindex ' +
						newEvent.getTreeIndex()
				);
			} else {
				console.log(
					'unloaded rank ' +
						newEvent.getRank() +
						'with treeIndex ' +
						newEvent.getTreeIndex()
				);
			}*/
			return newEvent;
		}
        return null;
	}
    startGiveRandomEvents() {
        this.#giveEvent = true;
    }
    stopGiveRandomEvents() {
        this.#giveEvent = false;
    }

	startAdvanced() {
		this.#advancedInterval = setInterval(() => {
			let newEvent = this.getRandomEvent();
			this.#jobStorage.addEvents([newEvent]);
		}, 500);
	}
	stopAdvanced() {
		window.clearInterval(this.#advancedInterval);
	}
	stopRandomEvent() {
		window.clearInterval(this.#interval);
	}

	useRemovedVertexFromLongJob() {
		let event1 = new Event(null, 131, 1, 5, 1);
		let event2 = new Event(null, 132, 0, 5, 1);
		let event3 = new Event(null, 130, 3, 5, 1);
		this.#jobStorage.addEvents([event1, event2, event3]);
	}

	useVertexDouble() {
		let event1 = new Event(null, 10, 0, 5, 1);
		let event2 = new Event(null, 20, 1, 5, 1);
		let event3 = new Event(null, 30, 3, 5, 1);
		let event4 = new Event(null, 40, 100, 5, 1);
		let event5 = new Event(null, 30, 2, 5, 1);
		this.#jobStorage.addEvents([event1, event2, event3, event4, event5]);
		let job = this.#jobStorage.getJob(5);
		console.log(job);
	}
	useTreeIndexDouble() {
		let event1 = new Event(null, 10, 0, 5, 1);
		let event2 = new Event(null, 20, 1, 5, 1);
		let event3 = new Event(null, 30, 3, 5, 1);
		let event4 = new Event(null, 40, 1, 5, 1);
		let event5 = new Event(null, 50, 1, 5, 1);
		this.#jobStorage.addEvents([event1, event2, event3, event4, event5]);
		let job = this.#jobStorage.getJob(5);
		console.log(job);
	}
	useTreeIndexDoubleSequentiell() {
		let event1 = new Event(null, 10, 0, 5, 1);
		let event2 = new Event(null, 20, 1, 5, 1);
		let event3 = new Event(null, 30, 3, 5, 1);
		//let event6 = new Event(null, 30, 1, 5,0);

		let event4 = new Event(null, 40, 1, 5, 1);
		//let event7 = new Event(null, 40, 1, 5,0);
		let event5 = new Event(null, 50, 1, 5, 1);
		this.#jobStorage.addEvents([event1]);
		this.#jobStorage.addEvents([event2]);
		this.#jobStorage.addEvents([event3]);
		//this.#jobStorage.addEvents([event6]);

		this.#jobStorage.addEvents([event4]);
		//this.#jobStorage.addEvents([event7]);
		this.#jobStorage.addEvents([event5]);
		let job = this.#jobStorage.getJob(5);
		console.log(job);
	}

	render() {
		return (
			<div>
				VisTests
				<div>
					<button onClick={() => console.log(this.#jobStorage.getJob(6))}>
						Print job 6
					</button>
					<button onClick={() => this.testJobs()}>Test add job</button>
					<br></br>
					<button onClick={() => this.addLongTestJob()}>Add long job</button>
					<br></br>
					<button onClick={() => this.removeFromLongJob()}>
						Remove from long job
					</button>
					<br></br>
					<button onClick={() => this.addFullJob()}>Add fulll job</button>
					<br></br>
					<button onClick={() => this.useRemovedVertexFromLongJob()}>
						Reuse node
					</button>
					<br></br>
					<button onClick={() => this.startAddRandomEvents()}>
						Start random events
					</button>
					<br></br>
					<button onClick={() => this.stopRandomEvent()}>
						Stop random events
					</button>
					<br></br>
					<button onClick={() => this.useVertexDouble()}>Use double</button>
					<br></br>
					<button onClick={() => this.useTreeIndexDouble()}>
						Use index double
					</button>
					<br></br>

					<button onClick={() => this.useTreeIndexDoubleSequentiell()}>
						Sequentiell
					</button>
					<br></br>
					<button onClick={() => this.startAdvanced()}>
						Start random advanced events
					</button>
					<br></br>
					<button onClick={() => this.stopAdvanced()}>
						Stop random advanced events
					</button>

				</div>
			</div>
		);
	}
}
