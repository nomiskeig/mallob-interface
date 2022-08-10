import React, { useState, useEffect, useContext } from 'react';
import './SubmitPage.scss';

import { DependencyTable } from '../../global/dependencyTable/DependencyTable';
import { InputWithLabel } from '../../global/input/InputWithLabel';
import { DropdownComponent } from '../../global/dropdown/DropdownComponent';
import { Header } from '../../global/header/Header';
import { JobContext } from '../../context/JobContextProvider';
import { UserContext } from '../../context/UserContextProvider';
import {
	getIndexByParam,
	configParameters,
	INPUT_TYPE_TEXT,
	INPUT_TYPE_SELECT,
	INPUT_TYPE_BOOLEAN,
} from '../jobPage/Parameters';

function submitJob() {}
export function SubmitPage(props) {
	let jobContext = useContext(JobContext);
	let userContext = useContext(UserContext);
	let [jobToSubmit, setJobToSubmit] = useState({});
	let [selectedOptionalIndices, setSelectedOptionalIndices] = useState([]);
	useEffect(() => {
		jobContext.loadAllJobsOfUser();
	}, []);
	let ownJobs = jobContext.jobs.filter(
		(job) => job.user === userContext.user.username
	);

	function getInputBasedOnParam(param) {
		switch (param.inputType) {
			case INPUT_TYPE_TEXT:
				return (
					<InputWithLabel
						labelText={param.name}
						value={
							jobToSubmit[param.internalName]
								? ''
								: jobToSubmit[param.internalName]
						}
						onChange={(newValue) => {
							param.updateSubmitJob(jobToSubmit, newValue);
							setJobToSubmit(jobToSubmit);
						}}
					></InputWithLabel>
				);
			case INPUT_TYPE_SELECT:
				return (
					<DropdownComponent
						title={param.name}
						items={param.selectValues.map((value) => ({
							onClick: () => {
                                jobToSubmit[param.internalName] = value;
								setJobToSubmit(jobToSubmit);
							},
							name: value,
						}))}
                        displaySelectedValue={true}
					></DropdownComponent>
				);
		}
	}
	let requiredParamsInputs = configParameters
		.filter((param) => param.required)
		.map((param) => {
			return getInputBasedOnParam(param);
		});
	/*let optionalParamInputs = 
		configParameters
			.filter((param) => !param.required)
			.filter((param, index) => selectedOptionalParams.includes(index))
			.map((param) => getInputBasedOnParam(param))
*/
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
								{requiredParamsInputs}
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
					<div className='col-12 col-md-6'>
						<div className='submitPagePanel lowerPanel lowerPanelLeft'>
							<Header title={'Description'} />
						</div>
					</div>
					<div className='col-12 col-md-6'>
						<div className='submitPagePanel lowerPanel lowerPanelRight'>
							<Header title={'Dependencies'} />
							<DependencyTable
								dependencies={ownJobs}
								input={true}
								onChange={(selectedJobIDs) => {
									selectedJobIDs = selectedJobIDs;
								}}
							/>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}
