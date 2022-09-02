import { useFilePicker } from 'use-file-picker';
import React, { useState, useEffect } from 'react';
import './FileDescrption.scss';
export function FileDescription(props) {
	let [openFileSelector, { plainFiles, clear }] = useFilePicker({
		accept: '.cnf',
	});
	let [files, setFiles] = useState([]);

	useEffect(() => {
		if (plainFiles.length === 0) {
			return;
		}
		let newFiles = [...files];
		plainFiles.forEach((file) => newFiles.push(file));
		setFiles(newFiles);
		props.setDescriptions(newFiles);
		clear();
	}, [plainFiles]);
	let selectedFilesDisplay = files.map((file,index) => {
		return <div key={index} className='selectedFileDisplay'>{file.name}</div>;
	});

	return (
		<div
			data-testid='fileDescription'
			className='fileDescriptionContainer d-flex flex-column'
		>
			<button
				className='btn btn-primary addDescriptionButton'
				onClick={() => openFileSelector()}
			>
				Add description
			</button>
			<button
				style={{ display: 'none' }}
				data-testid='fileDescriptionTestButton'
				onClick={() => {
                    plainFiles = [1]
                    let newFiles = [...files];
                    newFiles.push(new File(['some file data'], 'testFile.cnf'))
					setFiles(newFiles)
                    
				}}
			/>
			{selectedFilesDisplay.length >= 1 && (
				<React.Fragment>
					<div className='selectedFilesHeader'>Selected files:</div>
					<div className='selectedFilesContainer'>{selectedFilesDisplay}</div>
				</React.Fragment>
			)}
		</div>
	);
}
