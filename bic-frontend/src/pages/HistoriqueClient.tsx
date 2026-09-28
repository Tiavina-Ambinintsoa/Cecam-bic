import { useState } from 'react';

export function HistoriqueClient() {
    const [value, setValue] = useState(0);

    return (
        <button onClick={() => setValue(value + 1)}>
            Clicked {value} times
        </button>
    );
}