import React from 'react';
import { act } from 'react-dom/test-utils';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { AllContextProvider, AllContext } from './AllContextProvider';
import {InfoContextProvider, InfoContext, TYPE_UNRECOVERABLE, TYPE_WARNING} from './InfoContextProvider'
import { JobContextProvider } from './JobContextProvider';
import axios from 'axios';
import { UserContext } from './UserContextProvider';
jest.mock('axios');
jest.mock('./UserContextProvider.jsx');

function DummyDisplay(props) {
	return (
		<InfoContextProvider>
			<InfoContext.Consumer>
            {({handleInformation, dismissInformation}) =>( 
                    <div>
                        <button onClick={() => handleInformation("Unrecoverable error", TYPE_UNRECOVERABLE)}>UnrecoverableButton</button>
                        <button onClick={() => handleInformation("Warning", TYPE_WARNING)}>OtherButton</button>
                    </div>
                )}
			</InfoContext.Consumer>
		</InfoContextProvider>
	);
}


test('displays error page when unrecoverable', async () => {
	render(<DummyDisplay />);
    expect(screen.queryByText(/Unrecoverable error/)).toBeNull();
    await waitFor(() => {
        let button = screen.getByRole('button', {name: "UnrecoverableButton"});
        fireEvent.click(button);
    })
    expect(screen.getByText(/Unrecoverable error/)).not.toBeNull();


});

test('displays error message and error message can be closed again', async () =>{
    render(<DummyDisplay/>);
    expect(screen.queryByText(/Warning/)).toBeNull();
    await waitFor(() => {
        let button = screen.getByRole('button', {name: "OtherButton"});
        fireEvent.click(button);
    })
    expect(screen.queryByText(/Warning/)).not.toBeNull();
    await waitFor(() => {
        let button = screen.getByTestId("alertCloseButton");
        fireEvent.click(button);
    })
    expect(screen.queryByText(/Warning/)).toBeNull();
})
