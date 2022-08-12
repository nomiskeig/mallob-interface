import { DropdownComponent } from '../dropdown/DropdownComponent';
import { TextFieldDescription } from './TextFieldDescription';
import { FileDescription } from './FileDescription';
import React, { useState } from 'react';
import './Description.scss';
export const DESCRIPTION_TEXT_FIELD = 'textFieldDescription';
export const DESCRIPTION_FILE = 'fileDescription';
export function Description(props) {
	let [descriptionKind, setDescriptionType] = useState(DESCRIPTION_TEXT_FIELD);

	let specificDescription;
	let dropdownItems = [
		{
			onClick: () => {
				setDescriptionType(DESCRIPTION_TEXT_FIELD);
				props.setDescriptionType(DESCRIPTION_TEXT_FIELD);
			},
			name: 'Input-Field',
		},
		{
			onClick: () => {
				setDescriptionType(DESCRIPTION_FILE);
				props.setDescriptionType(DESCRIPTION_FILE);
			},
			name: 'File',
		},
	];

	switch (descriptionKind) {
		case DESCRIPTION_TEXT_FIELD:
			specificDescription = (
				<TextFieldDescription setDescriptions={props.setDescriptions} />
			);
			break;
		case DESCRIPTION_FILE:
			specificDescription = (
				<FileDescription setDescriptions={props.setDescriptions} />
			);
			break;
        default:
            specificDescription = <React.Fragment></React.Fragment>
	}
	return (
		<div className='descriptionContainer d-flex flex-column'>
			<DropdownComponent
				title=''
				items={dropdownItems}
				displaySelectedValue={true}
				default={'Input-Field'}
			/>
			{specificDescription}
		</div>
	);
}
