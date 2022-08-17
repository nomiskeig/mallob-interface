export const INPUT_TYPE_TEXT = 'text';
export const INPUT_TYPE_SELECT = 'select';
export const INPUT_TYPE_BOOLEAN = 'boolean'
export const INPUT_TYPE_NONE  ='none';
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
            return {
                isValid: true
            }
        }
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
                    reason: 'Application is required.'
                }
            }
            return {
                isValid: true
            }

        }

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
            return {
                isValid: true
            }
        }

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
            return {
                isValid: true
            }
        }
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
            return {
                isValid: true
            }
        }
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
                isValid: true
            }
        }
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
                isValid: true
            }
        }

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
            return {
                isValid: true
            }
        }
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
                isValid: true
            }
        }
	},
    {
        name: 'Name',
        internalName: 'name',
        path: ['config', 'name'],
        index: 9,
        width: 200,
        validateValue: (value) => {
           if (value === undefined  || value === '')  {
                return {
                    isValid: false,
                    reason: 'Name is required.'
                }
            }
            return {
                isValid: true
            }

        },
        required: true,
        inputType: INPUT_TYPE_TEXT,
        showOnJobPage: false


        
    },
    {
        name: 'Parsing Time',
        internalName: 'parsing',
        path: ['resultData', 'time', 'parsing'],
        index: 10,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    },
    {
        name: 'Processing Time',
        internalName: 'processing',
        path: ['resultData', 'time', 'processing'],
        index: 11,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    },
    {
        name: 'Scheduling Time',
        internalName: 'scheduling',
        path: ['resultData', 'time', 'scheduling'],
        index: 12,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    },
    {
        name: 'Total Time',
        internalName: 'totalTime',
        path: ['resultData', 'time', 'total'],
        index: 13,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    },
    {
        name: 'Used CPU Seconds',
        internalName: 'usedCPUSeconds',
        path: ['resultData', 'usedCpuSeconds'],
        index: 14,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    },
    {
        name: 'Used Wallclock Seconds',
        internalName: 'usedWallclockSeconds',
        path: ['resultData', 'usedWallclockSeconds'],
        index: 15,
        width: 200,
        inputType: INPUT_TYPE_NONE,
        showOnJobPage: true

    }
];
export function getIndexByParam(paramToFind) {
    return configParameters.findIndex(param => param.internalName === paramToFind.internalName);

}
