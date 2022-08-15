import React, { useState, useEffect, useContext } from 'react';
import './SubmitPage.scss';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import {
	Description,
	DESCRIPTION_FILE,
} from '../../global/description/Description';
import { Header } from '../../global/header/Header';
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import {
	InfoContext,
	TYPE_INFO,
	TYPE_WARNING,
} from '../../context/InfoContextProvider';
import {
	getIndexByParam,
	configParameters,
	INPUT_TYPE_TEXT,
	INPUT_TYPE_SELECT,
	INPUT_TYPE_BOOLEAN,
} from '../jobPage/Parameters';
import { DESCRIPTION_TEXT_FIELD } from '../../global/description/Description';
const FormData = require('form-data');

/**
 * Validates the given paramters.
 *
 * @param {[TODO:type]} parameters - [TODO:description]
 * @returns {[TODO:type]} [TODO:description]
 */
function validateInput(parameters) {
	let validateErrors = [];
	configParameters.forEach((param) => {
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
	useEffect(() => {
		jobContext.loadAllJobsOfUser();
		// eslint-disable-next-line react-hooks/exhaustive-deps
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
		job['description'] = [...descriptions];
		if (dependencies.length > 0) {
			job['dependencies'] = [...dependencies];
		}

		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH + '/api/v1/jobs/submit/inclusive',
			data: job,
		}).then((res) => {
			infoContext.handleInformation('Job successfully submitted.', TYPE_INFO);
			navigate('/job/' + res.data.jobID);
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

		console.log(descriptions);
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

		axios({
			method: 'post',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/jobs/submit/exclusive/config',
			data: job,
		}).then((res) => {
			infoContext.handleInformation('Job successfully submitted.', TYPE_INFO);
			navigate('/job/' + res.data.jobID);
		});
	}

	function getInputBasedOnParam(param) {
		switch (param.inputType) {
			case INPUT_TYPE_TEXT:
				return (
					<InputWithLabel
						key={getIndexByParam(param)}
						labelText={param.name}
						value={
							jobToSubmit[param.internalName]
								? jobToSubmit[param.internalName]
								: ''
						}
						onChange={(newValue) => {
							let newJobToSubmit = { ...jobToSubmit };
							console.log(newJobToSubmit);
							newJobToSubmit[param.internalName] = newValue;

							setJobToSubmit(newJobToSubmit);
						}}
					></InputWithLabel>
				);
			case INPUT_TYPE_SELECT:
				return (
					<DropdownComponent
						key={getIndexByParam(param)}
						title={param.name}
						items={param.selectValues.map((value) => ({
							onClick: () => {
                                let newJobToSubmit = {...jobToSubmit};
                                newJobToSubmit[param.internalName] = value;
								setJobToSubmit(newJobToSubmit);
							},
							name: value,
						}))}
						displaySelectedValue={true}
					></DropdownComponent>
				);
			case INPUT_TYPE_BOOLEAN:
                if (jobToSubmit[param.internalName] === undefined) {
                    let newJobToSubmit = {...jobToSubmit}
                    newJobToSubmit[param.internalName] = false;
                    setJobToSubmit(newJobToSubmit);
                }
				return (
					<div
						key={getIndexByParam}
						className='d-flex flex-column align-items-start booleanInputContainer'
					>
						<label className='booleanInputLabel'>{param.name}</label>
						<input
							type='checkbox'
							className='form-check-input booleanInputCheckbox'
                            checked={jobToSubmit[param.internalName]}
                            onChange={() =>{ 
                                let newJobToSubmit = {...jobToSubmit};
                                newJobToSubmit[param.internalName] = !jobToSubmit[param.internalName];
                                setJobToSubmit(newJobToSubmit);
                            }}
						></input>
					</div>
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
	let selectAdditionalParamsItems = configParameters
		.filter((param) => !param.required)
		.filter(
			(param) => !selectedOptionalIndices.includes(getIndexByParam(param))
		)
		.map((param) => ({
			onClick: () => {
				let newSelectionOptionalIndices = [...selectedOptionalIndices];
				newSelectionOptionalIndices.push(getIndexByParam(param));
				console.log(newSelectionOptionalIndices);
				setSelectedOptionalIndices(newSelectionOptionalIndices);
			},
			name: param.name,
		}));

	return (
		<div className='submitPageContainer'>
			<div className='marginContainer'>
				<div className='row g-0 submitPageRow'>
					<div className='col submitPageCol'>
						<div className='submitPagePanel row g-0 upperPanel'>
							<div className='requiredParamsContainer col-md-3'>
								<Header title={'Required'} />
								<div className='requiredParamsFlex d-flex flex-column'>{requiredParamsInputs}</div>
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
								input={true}
								onChange={(selectedJobIDs) => {
									setDependencies(selectedJobIDs);
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
