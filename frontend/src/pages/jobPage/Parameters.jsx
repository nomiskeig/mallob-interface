export const INPUT_TYPE_TEXT = 'text';
export const INPUT_TYPE_SELECT = 'select';
export const INPUT_TYPE_BOOLEAN = 'boolean';
export const INPUT_TYPE_NONE = 'none';
export const configParameters = [
	{
		name: 'Priority',
		path: ['config', 'priority'],
		internalName: 'priority',
		width: 140,
		index: 0,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
			console.log(value);
			if (value === undefined) {
				return {
					isValid: true,
				};
			} else if (Number.isNaN(Number.parseFloat(value))) {
				return {
					isValid: false,
					reason: 'Priority has to be a number.',
				};
			}
			if (value <= 0) {
				return {
					isValid: false,
					reason: 'Priority has to be bigger than zero.',
				};
			}

			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Application',
		path: ['config', 'application'],
		internalName: 'application',
		width: 150,
		index: 1,
		required: true,
		inputType: INPUT_TYPE_SELECT,
		selectValues: ['SAT', 'DUMMY', 'KMEANS'],
		showOnJobPage: true,
		validateValue: (value) => {
			if (value === undefined) {
				return {
					isValid: false,
					reason: 'Application is required.',
				};
			}
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Maximum Demand',
		path: ['config', 'maxDemand'],
		internalName: 'maxDemand',
		width: 200,
		index: 2,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
			if (value === undefined) {
				return {
					isValid: true,
				};
			} else if (!Number.isInteger) {
                return {
                    isValid: false,
                    reason: 'Maximum Demand has to be an integer.'
                }
            }
            if (value < 0) {
                return {
                    isValid: false,
                    reason: 'Maximum Demand has to be at least zero.'
                }
            }

			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Wallclock-Limit',
		path: ['config', 'wallclockLimit'],
		internalName: 'wallclockLimit',
		width: 174,
		index: 3,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
            if (value === undefined) {
                return {
                    isValid: true
                }
            }
            let regex = /^(([123456789]\d*)|0)(ms|s|m|h|d)$/
            if (!regex.test(value)) {
                return {
                    isValid: false,
                    reason: 'Wallclock-Limit has incorrect format'
                }
            }
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'CPU-Limit',
		path: ['config', 'cpuLimit'],
		internalName: 'cpuLimit',
		width: 140,
		index: 4,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
            if (value === undefined) {
                return {
                    isValid: true
                }
            }
            let regex = /^(([123456789]\d*)|0)(ms|s|m|h|d)$/
            if (!regex.test(value)) {
                return {
                    isValid: false,
                    reason: 'CPU-Limit has incorrect format'
                }
            }
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Arrival',
		path: ['config', 'arrival'],
		internalName: 'arrival',
		width: 300,
		index: 5,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Dependencies',
		path: ['config', 'dependencies'],
		internalName: 'dependencies',
		width: 200,
		index: 6.4,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: false,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Incremental',
		path: ['config, incremental'],
		internalName: 'incremental',
		width: 150,
		index: 6,
		required: false,
		inputType: INPUT_TYPE_BOOLEAN,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Precursor',
		path: ['config', 'precursor'],
		internalName: 'precursor',
		width: 140,
		index: 7,
		required: false,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: true,
		validateValue: (value) => {
            if (value === undefined) {
                return {
                    isValid: true
                };
            }
            if (!Number.isInteger(value)) {
                return {
                    isValid: false,
                    reason: "Precursor has to be an integer."
                }
            }
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Content-Mode',
		path: ['config', 'contentMode'],
		internalName: 'contentMode',
		width: 170,
		index: 8,
		required: false,
		inputType: INPUT_TYPE_SELECT,
		selectValues: ['raw', 'text'],
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Name',
		internalName: 'name',
		path: ['config', 'name'],
		index: 9,
		width: 200,
		validateValue: (value) => {
			if (value === undefined || value === '') {
				return {
					isValid: false,
					reason: 'Name is required.',
				};
			}
			return {
				isValid: true,
			};
		},
		required: true,
		inputType: INPUT_TYPE_TEXT,
		showOnJobPage: false,
		transformOutput: (value) => value,
	},
	{
		name: 'Additional config',
		internalName: 'additionalConfig',
		path: ['config', 'additionalConfig'],
		index: 9.5,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: false,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => JSON.stringify(value),
	},
	{
		name: 'Parsing Time',
		internalName: 'parsing',
		path: ['resultData', 'time', 'parsing'],
		index: 10,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Processing Time',
		internalName: 'processing',
		path: ['resultData', 'time', 'processing'],
		index: 11,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Scheduling Time',
		internalName: 'scheduling',
		path: ['resultData', 'time', 'scheduling'],
		index: 12,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Total Time',
		internalName: 'totalTime',
		path: ['resultData', 'time', 'total'],
		index: 13,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Used CPU Seconds',
		internalName: 'usedCPUSeconds',
		path: ['resultData', 'usedCpuSeconds'],
		index: 14,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
	{
		name: 'Used Wallclock Seconds',
		internalName: 'usedWallclockSeconds',
		path: ['resultData', 'usedWallclockSeconds'],
		index: 15,
		width: 200,
		inputType: INPUT_TYPE_NONE,
		showOnJobPage: true,
		validateValue: (value) => {
			return {
				isValid: true,
			};
		},
		transformOutput: (value) => value,
	},
];
export function getIndexByParam(paramToFind) {
	return configParameters.findIndex(
		(param) => param.internalName === paramToFind.internalName
	);
}
