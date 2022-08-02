import React, {useState, useEffect, useContext} from 'react';
import {useParams} from 'react-router-dom';
export function JobPage(props) {
    let {jobID} = useParams();
    let jobContext = useContext()

    useEffect(() => {

    })
    

    return  <div>JobPage with id :{jobID} </div>
}
