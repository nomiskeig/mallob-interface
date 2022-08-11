import { DropdownComponent } from '../dropdown/DropdownComponent';
import { TextFieldDescription } from './TextFieldDescription';
import {FileDescription} from './FileDescription';
import { useState } from 'react';
import './Description.scss';
const DESCRIPTION_TEXT_FIELD = 'textFieldDescription';
const DESCRIPTION_FILE = 'fileDescription';
export function Description() {
	let [descriptionKind, setDescriptionType] = useState(DESCRIPTION_TEXT_FIELD);

	let specificDescription;
	let dropdownItems = [
		{
			onClick: () => setDescriptionType(DESCRIPTION_TEXT_FIELD),
			name: 'Input-Field',
		},
		{
			onClick: () => setDescriptionType(DESCRIPTION_FILE),
			name: 'File',
		},
	];

	switch (descriptionKind) {
		case DESCRIPTION_TEXT_FIELD:
			specificDescription = <TextFieldDescription />;
            break;
        case DESCRIPTION_FILE: 
            specificDescription = <FileDescription />;
            break;
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
