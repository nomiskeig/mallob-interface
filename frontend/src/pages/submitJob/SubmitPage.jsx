import React, { useState, useEffect, useContext } from 'react';
import Tooltip from '@mui/material/Tooltip';
import './SubmitPage.scss';
import axios from 'axios';
import format from 'date-fns/format'
import { useNavigate } from 'react-router-dom';
import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import { AdditionalParam } from '../../global/input/AdditionalParam';
import {
	Description,
	DESCRIPTION_FILE,
} from '../../global/description/Description';
import { Header } from '../../global/header/Header';
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import {
	InfoContext,
	TYPE_ERROR,
	TYPE_INFO,
	TYPE_WARNING,
} from '../../context/InfoContextProvider';
import {
	getIndexByParam,
	configParameters,
	INPUT_TYPE_TEXT,
	INPUT_TYPE_SELECT,
	INPUT_TYPE_BOOLEAN,
	INPUT_TYPE_DATETIME,
	INPUT_TYPE_NONE,
} from '../jobPage/Parameters';
import { DESCRIPTION_TEXT_FIELD } from '../../global/description/Description';
const FormData = require('form-data');
const TOOLTIP_ENTER_DELAY = 1000;

/**
 * Validates the given paramters.
 *
 */
function validateInput(parameters) {
	let validateErrors = [];
	configParameters
		.filter((param) => param.inputType !== INPUT_TYPE_NONE)
		.forEach((param) => {
			let result = param.validateValue(parameters[param.internalName]);
			if (!result.isValid) {
				validateErrors.push(result.reason);
			}
		});
	return validateErrors;
}
export function SubmitPage(props) {
	let jobContext = useContext(JobContext);
	let userContext = useContext(UserContext);
	let infoContext = useContext(InfoContext);
	let navigate = useNavigate();
	let [dependencies, setDependencies] = useState([]);
	let [jobToSubmit, setJobToSubmit] = useState({});
	let [selectedOptionalIndices, setSelectedOptionalIndices] = useState([]);
	let [descriptions, setDescriptions] = useState([]);
	let [descriptionKind, setDescriptionKind] = useState(DESCRIPTION_TEXT_FIELD);
	let [additionalConfig, setAdditionalConfig] = useState([]);
	useEffect(() => {
		if (jobContext.jobToRestart === null) {
			return;
		}
		// fill up the page with the config of the job to restart
		let submittedJob = jobContext.jobs.filter(
			(job) => job.jobID == jobContext.jobToRestart
		)[0];
		if (submittedJob.config.dependencies) {
			setDependencies(submittedJob.config.dependencies);
			delete submittedJob.config.dependencies;
		}
		setJobToSubmit(submittedJob.config);
		if (submittedJob.config['arrival']) {
            try {
			let newJobToSubmit = submittedJob.config;
			newJobToSubmit['arrival'] = format(new Date(submittedJob.config['arrival']), "yyyy-MM-dd'T'HH:mm");
            setJobToSubmit(newJobToSubmit);
            } catch (e) {
                console.log(e.message)
            }
		}
		let newSelectionOptionalIndices = selectedOptionalIndices;
		configParameters
			.filter((param) => !param.required)
			.forEach((param) => {
				if (submittedJob.config[param.internalName]) {
					newSelectionOptionalIndices.push(getIndexByParam(param));
				}
			});
		if (submittedJob.config.additionalConfig) {
			let index = 0;
			let newAdditionalConfig = additionalConfig;
			for (let [key, value] of Object.entries(
				submittedJob.config.additionalConfig
			)) {
				newAdditionalConfig.push({ index: index, key: key, value: value });
				index += 1;
			}
			setAdditionalConfig([...newAdditionalConfig]);
		}
		setSelectedOptionalIndices([...newSelectionOptionalIndices]);
		jobContext.setJobToRestart(null);
	}, []);
	useEffect(() => {
		if (!userContext.user.isVerified) {
			navigate('/jobs');
			infoContext.handleInformation(
				'You can not submit a job as an unverified user.',
				TYPE_ERROR
			);
		}
		jobContext.loadAllJobsOfUser();
	}, []);
	let ownJobs = jobContext.jobs.filter(
		(job) => job.user === userContext.user.username
	);
	function submitJobInclusive() {
		let errors = validateInput(jobToSubmit);
		if (errors.length > 0) {
			errors.forEach((error) =>
				infoContext.handleInformation(error, TYPE_WARNING)
			);
			return;
		}

		if (descriptions.length === 0 || descriptions[0] === '') {
			infoContext.handleInformation('Description is required.', TYPE_WARNING);
			return;
		}

		let job = { ...jobToSubmit };

		if (job.arrival) {
			job.arrival = new Date(job.arrival).toISOString();
		}
		job['description'] = [...descriptions];
		if (dependencies.length > 0) {
			job['dependencies'] = [...dependencies];
		}
		job = addAddtionalParametersToJob(job);
		job = castParametersToCorrectTypes(job);
		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH + '/api/v1/jobs/submit/inclusive',
			data: job,
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				infoContext.handleInformation('Job successfully submitted.', TYPE_INFO);
				navigate('/job/' + res.data.jobID);
			})
			.catch((err) => {
				infoContext.handleInformation('Job could not be submitted', TYPE_ERROR);
			});
	}

	function submitJobExclusiveDescription() {
		let errors = validateInput(jobToSubmit);
		if (errors.length > 0) {
			errors.forEach((error) =>
				infoContext.handleInformation(error, TYPE_WARNING)
			);
		}

		if (descriptions.length === 0) {
			infoContext.handleInformation('Description is required.', TYPE_WARNING);
			return;
		}

		// submit description
		let formData = new FormData();
		descriptions.forEach((file, index) => {
			formData.append('file' + (index + 1), file);
		});

		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/submit/exclusive/description',
			data: formData,
			headers: {
				'Content-Type': 'multipart/form-data',
				Authorization: 'Bearer ' + userContext.user.token,
			},
		}).then((res) => {
			submitJobExclusiveConfig(res.data.descriptionID);
		});
	}

	function submitJobExclusiveConfig(descriptionID) {
		let job = { ...jobToSubmit };
		job['descriptionID'] = descriptionID;
		if (dependencies.length > 0) {
			job['dependencies'] = [...dependencies];
		}
		job = addAddtionalParametersToJob(job);
		job = castParametersToCorrectTypes(job);
		if (job.arrival) {
			job.arrival = new Date(job.arrival).toISOString();
		}

		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/submit/exclusive/config',
			data: job,
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		}).then((res) => {
			infoContext.handleInformation('Job successfully submitted.', TYPE_INFO);
			navigate('/job/' + res.data.jobID);
		});
	}
	function addAddtionalParametersToJob(job) {
		if (additionalConfig.length === 0) {
			return job;
		}
		additionalConfig.forEach((config) => {
			if (job['additionalConfig'] === undefined) {
				job['additionalConfig'] = {};
			}
			if (config.key !== '' && config.value !== '') {
				job['additionalConfig'][config.key] = config.value;
			}
		});
		return job;
	}
	function castParametersToCorrectTypes(job) {
		if (job['priority']) {
			job['priority'] = Number(job['priority']);
		}
		if (job['precursor']) {
			job['precursor'] = Number(job['precursor']);
		}
		if (job['maxDemand']) {
			job['maxDemand'] = Number(job['maxDemand']);
		}
		return job;
	}

	function getInputBasedOnParam(param) {
		switch (param.inputType) {
			case INPUT_TYPE_TEXT:
				return (
					<Tooltip
						key={getIndexByParam(param)}
						title={param.tooltipText}
						enterDelay={TOOLTIP_ENTER_DELAY}
					>
						<div>
							<InputWithLabel
								labelText={param.name}
								value={
									jobToSubmit[param.internalName]
										? jobToSubmit[param.internalName]
										: ''
								}
								onChange={(newValue) => {
									let newJobToSubmit = { ...jobToSubmit };
									newJobToSubmit[param.internalName] = newValue;

									setJobToSubmit(newJobToSubmit);
								}}
							></InputWithLabel>
						</div>
					</Tooltip>
				);
			case INPUT_TYPE_SELECT:
				return (
					<Tooltip
						key={getIndexByParam(param)}
						title={param.tooltipText}
						enterDelay={TOOLTIP_ENTER_DELAY}
					>
						<div>
							<DropdownComponent
								key={getIndexByParam(param)}
								title={param.name}
								items={param.selectValues.map((value) => ({
									onClick: () => {
										let newJobToSubmit = { ...jobToSubmit };
										newJobToSubmit[param.internalName] = value;
										setJobToSubmit(newJobToSubmit);
									},
									name: value,
								}))}
								default={jobToSubmit[param.internalName]}
								displaySelectedValue={true}
							></DropdownComponent>
						</div>
					</Tooltip>
				);
			case INPUT_TYPE_BOOLEAN:
				if (jobToSubmit[param.internalName] === undefined) {
					let newJobToSubmit = { ...jobToSubmit };
					newJobToSubmit[param.internalName] = false;
					setJobToSubmit(newJobToSubmit);
				}
				return (
					<Tooltip
						key={getIndexByParam(param)}
						title={param.tooltipText}
						enterDelay={TOOLTIP_ENTER_DELAY}
					>
						<div className='d-flex flex-column align-items-start booleanInputContainer'>
							<label className='booleanInputLabel'>{param.name}</label>
							<input
								data-testid={'inputCheckbox-' + param.name}
								type='checkbox'
								className='form-check-input booleanInputCheckbox'
								checked={jobToSubmit[param.internalName]}
								onChange={() => {
									let newJobToSubmit = { ...jobToSubmit };
									newJobToSubmit[param.internalName] =
										!jobToSubmit[param.internalName];
									setJobToSubmit(newJobToSubmit);
								}}
							></input>
						</div>
					</Tooltip>
				);

			case INPUT_TYPE_DATETIME:
				return (
					<Tooltip
						key={getIndexByParam(param)}
						title={param.tooltipText}
						enterDelay={TOOLTIP_ENTER_DELAY}
					>
						<div>
							<InputWithLabel
								labelText={'Arrival'}
								datetime={true}
								onChange={(newValue) => {
									let newJobToSubmit = { ...jobToSubmit };
									newJobToSubmit[param.internalName] = newValue;
									setJobToSubmit(newJobToSubmit);
								}}
								value={
									jobToSubmit[param.internalName]
										? jobToSubmit[param.internalName]
										: ''
								}
							></InputWithLabel>
						</div>
					</Tooltip>
				);
			default:
				return <div></div>;
		}
	}
	let requiredParamsInputs = configParameters
		.filter((param) => param.required)
		.map((param) => {
			return getInputBasedOnParam(param);
		});
	let optionalParamInputs = selectedOptionalIndices.map((index) =>
		getInputBasedOnParam(configParameters[index])
	);
	additionalConfig.forEach((config, index) => {
		optionalParamInputs.push(
			<AdditionalParam
				dataTestID={'inputAdditionalParam-' + index}
				keyValue={config.key}
				valueValue={config.value}
				onKeyChange={(newKey) => {
					let newAdditionalConfig = [...additionalConfig];
					newAdditionalConfig[config.index] = {
						index: config.index,
						key: newKey,
						value: config.value,
					};
					setAdditionalConfig(newAdditionalConfig);
				}}
				onValueChange={(newValue) => {
					let newAdditionalConfig = [...additionalConfig];
					newAdditionalConfig[config.index] = {
						index: config.index,
						key: config.key,
						value: newValue,
					};
					setAdditionalConfig(newAdditionalConfig);
				}}
			></AdditionalParam>
		);
	});
	let selectAdditionalParamsItems = configParameters
		.filter((param) => !param.required && param.inputType !== INPUT_TYPE_NONE)
		.filter(
			(param) => !selectedOptionalIndices.includes(getIndexByParam(param))
		)
		.map((param) => ({
			onClick: () => {
				let newSelectionOptionalIndices = [...selectedOptionalIndices];
				newSelectionOptionalIndices.push(getIndexByParam(param));
				setSelectedOptionalIndices(newSelectionOptionalIndices);
			},
			name: param.name,
		}));
	selectAdditionalParamsItems.push({
		onClick: () => {
			let newAdditionalConfig = [...additionalConfig];
			newAdditionalConfig.push({
				index: newAdditionalConfig.length,
				key: '',
				value: '',
			});
			setAdditionalConfig(newAdditionalConfig);
		},
		name: 'Key-Value-Parameter',
	});

	return (
		<div data-testid='baseDiv' className='submitPageContainer'>
			<div className='marginContainer'>
				<div className='row g-0 submitPageRow'>
					<div className='col submitPageCol'>
						<div className='submitPagePanel row g-0 upperPanel'>
							<div className='requiredParamsContainer col-md-3'>
								<Header title={'Required'} />
								<div className='requiredParamsFlex d-flex flex-column-reverse justify-content-end'>
									{requiredParamsInputs}
								</div>
							</div>
							<div className='optionalParamsContainer col-md-9'>
								<Header title={'Optional'} />
								<div className='optionalParamInputsContainer d-flex flex-wrap'>
									{optionalParamInputs}
									{selectAdditionalParamsItems.length >= 1 && (
										<DropdownComponent
											title={'Add option'}
											items={selectAdditionalParamsItems}
										/>
									)}
								</div>
							</div>
						</div>
					</div>
				</div>
				<div className='row g-0 submitPageRow'>
					<div className='col-12 col-md-6 submitPageCol'>
						<div className='submitPagePanel lowerPanel lowerPanelLeft d-flex flex-column'>
							<Header title={'Description'} />
							<Description
								setDescriptions={(descriptions) =>
									setDescriptions(descriptions)
								}
								setDescriptionKind={(descriptionKind) =>
									setDescriptionKind(descriptionKind)
								}
							/>
						</div>
					</div>
					<div className='col-12 col-md-6 submitPageCol d-flex flex-column'>
						<div className='submitPagePanel lowerPanel lowerPanelRight'>
							<Header title={'Dependencies'} />
							<DependencyTable
								dependencies={ownJobs}
								selectedIDs={dependencies}
								input={true}
								onChange={(selectedJobIDs) => {
									setDependencies([...selectedJobIDs]);
								}}
							/>
						</div>
						<div className='submitButtonContainer flex-grow-1 d-flex flex-row-reverse'>
							<button
								className='btn btn-success submitButton'
								onClick={() => {
									if (descriptionKind === DESCRIPTION_TEXT_FIELD) {
										submitJobInclusive();
									} else if (descriptionKind === DESCRIPTION_FILE) {
										submitJobExclusiveDescription();
									}
								}}
							>
								Submit job
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
