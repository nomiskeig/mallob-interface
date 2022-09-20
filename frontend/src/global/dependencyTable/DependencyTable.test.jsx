import { DependencyTable } from './DependencyTable';
import { fireEvent, render, screen } from '@testing-library/react';

const onChange = jest.fn(selectedIDs => selected = selectedIDs);
let selected =[]
const dependencies = [
	{
		config: {
			name: 'job1',
		},
		jobID: 1,
	},
	{
		config: {
			name: 'job2',
		},
		jobID: 2,
	},
];
beforeEach(()=>{
    jest.resetAllMocks();
    selected = [];
})


test('Displays the dependencies', () => {
	render(<DependencyTable dependencies={dependencies} />);
	expect(screen.getAllByText(/job[1,2]/)).toHaveLength(2);
});

test('Can select jobs when set to input', () => {
    render(<DependencyTable dependencies={dependencies} onChange={onChange} selectedIDs={selected} input={true}/>);
    const checkboxJob1 = screen.getByTestId('dependencyTableCheckbox-1');
    fireEvent.click(checkboxJob1);
    expect(onChange).toHaveBeenCalledTimes(1);
    expect(onChange).toHaveBeenCalledWith([1]);
    const checkboxJob2 = screen.getByTestId('dependencyTableCheckbox-2');
    fireEvent.click(checkboxJob2);
    expect(onChange).toHaveBeenCalledTimes(2);
    expect(onChange).toHaveBeenCalledWith([1,2]);
    fireEvent.click(checkboxJob1);
    expect(onChange).toHaveBeenCalledTimes(3);
    expect(onChange).toHaveBeenCalledWith([2]);

});
