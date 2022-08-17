import {InputWithLabel} from './InputWithLabel'
export function AdditionalParam(props) {

    return <div className='addtionalParamContainer'>

        <InputWithLabel labelText={'Key'} value={props.keyValue} onChange={(text) => props.onKeyChange(text)}/>
        <InputWithLabel labelText={'Value'} value={props.valueValue} onChange={(text) => props.onValueChange(text)}/>
    </div>

}
