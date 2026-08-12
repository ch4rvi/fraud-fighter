import { useState } from 'react'
import './App.css'

function App() {
  const [debtorAccountNumber, setDebtorAccountNumber] = useState("")
  const [debtorBankCode, setDebtorBankCode] = useState("")
  const [creditorAccountNumber, setCreditorAccountNumber] = useState("")
  const [creditorBankCode, setCreditorBankCode] = useState("")
  const [currency, setCurrency] = useState("")
  const [amount, setAmount] = useState("")

  const sendTransactionRequest = {
    "debtorAccount": {
      "debtorAccountNumber": debtorAccountNumber,
      "debtorBankCode": debtorBankCode,
    },
    "creditorAccount": {
      "creditorAccountNumber": creditorAccountNumber,
      "creditorBankCode": creditorBankCode,
    },
    "currency": currency,
    "amount": amount,
    "source": "IB"
  }

  const sendTransaction = () => {
    fetch(
        "http://localhost:8080/api/fraud/transaction",
        {
          "method": "POST",
          "headers": {"Content-Type": "application/json"},
          "body": JSON.stringify(sendTransactionRequest)
        }
    );
  }

  return (
    <>
      <section id="center">
        <div>
          <h1>SEND TRN</h1>
          <p>
            Fill in the form to send transaction.
          </p>
        </div>
        <input name="debtorAccountNumber"
               type="text"
               value={debtorAccountNumber}
               onChange={(event) => setDebtorAccountNumber(event.target.value)}></input>
        <input name="debtorBankCode"
               type="text"
               value={debtorBankCode}
               onChange={(event) => setDebtorBankCode(event.target.value)}></input>
        <input name="creditorAccountNumber"
               type="text"
               value={creditorAccountNumber}
               onChange={(event) => setCreditorAccountNumber(event.target.value)}></input>
        <input name="creditorBankCode"
               type="text"
               value={creditorBankCode}
               onChange={(event) => setCreditorBankCode(event.target.value)}></input>
        <input name="currency"
               type="text"
               value={currency}
               onChange={(event) => setCurrency(event.target.value)}></input>
        <input name="amount"
               type="text"
               value={amount}
               onChange={(event)=> setAmount(event.target.value)}></input>
        <input name="submit" type="submit"
               onClick={sendTransaction}></input>


        <p>Vepsaná hodnota je {debtorAccountNumber}</p>
        <p>Vepsaná hodnota je {debtorBankCode}</p>
        <p>Vepsaná hodnota je {creditorAccountNumber}</p>
        <p>Vepsaná hodnota je {creditorBankCode}</p>
        <p>Vepsaná hodnota je {currency}</p>
        <p>Vepsaná hodnota je {amount}</p>

      </section>
    </>
  )
}

export default App
