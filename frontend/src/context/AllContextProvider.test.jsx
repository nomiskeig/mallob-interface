import React from 'react';
import { act } from 'react-dom/test-utils';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { AllContextProvider, AllContext } from './AllContextProvider';
import { JobContextProvider } from './JobContextProvider';
import axios from 'axios';
import { UserContext } from './UserContextProvider';
jest.mock('axios');
jest.mock('./UserContextProvider.jsx');

function DummyDisplay(props) {
	return (
		<AllContextProvider>
			<AllContext.Consumer>
				{(value) => <div>{getList(value)}</div>}
			</AllContext.Consumer>
		</AllContextProvider>
	);
}

function getList(values) {
	let list = [];
	for (const [key, value] of Object.entries(values)) {
		list.push(key);
	}
		return list;
}

test('Context should have a default value', () => {
	render(<DummyDisplay />);
	screen.debug();
    expect(screen.getByText(/userContext/)).not.toBeNull();
    expect(screen.getByText(/infoContext/)).not.toBeNull();
    expect(screen.getByText(/jobContext/)).not.toBeNull();
    expect(screen.getByText(/settingsContext/)).not.toBeNull();

});
