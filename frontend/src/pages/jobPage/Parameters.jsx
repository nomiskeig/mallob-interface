export const INPUT_TYPE_TEXT = 'text';
export const INPUT_TYPE_SELECT = 'select';
export const INPUT_TYPE_BOOLEAN = 'boolean'
export const configParameters = [
	{
		name: 'Priority',
		path: ['config', 'priority'],
		internalName: 'priority',
		width: 40,
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
		width: 100,
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
		width: 100,
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
		width: 100,
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
		width: 100,
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
		width: 100,
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
		width: 50,
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
		width: 100,
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


        
    }
];
export function getIndexByParam(paramToFind) {
    return configParameters.findIndex(param => param.internalName === paramToFind.internalName);

}
