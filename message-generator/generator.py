import json
import requests
import random

config = {'host': 'localhost',
          'port': 8080}


class Sender:
    def __init__(self, config):
        self.config = config
        self.headers = {"Content-Type":"application/json"}

    @staticmethod
    def __serialize_if_needed(message):
        if isinstance(message, str):
            return message

        return json.dumps(message)

    def send_book(self, data):
        payload = self.__serialize_if_needed(data)
        return requests.post(url='http://localhost:8080/api/v1/users/book-phone', headers=self.headers, data=payload)

    def send_return(self, data):
        payload = self.__serialize_if_needed(data)
        return requests.post(url='http://localhost:8080/api/v1/users/return-phone', headers=self.headers, data=payload)


def main():
    sender = Sender(config)

    phones = [
        '2dc1471d-236f-4614-8f4a-da10d7f06d04',
        '0063d120-f501-43f2-8545-7c7c00ce7b38',
        '7ff262b9-4ef2-4edb-94b1-6dccc6fa3d1a',
        '25a7865d-bf3c-4ac3-a7a0-03831eb818cc',
        'ba446d46-7271-461c-bef1-8b0f7252cefc',
        '08969c4e-3933-4c12-9450-5174f57a7db3',
        'cc23fe8e-fc7f-4ba4-8b46-4c1ce65d7368',
        '0ab6703d-16cc-47a5-82b8-7a7a010ddd67',
        'ca7d93b2-a664-4c8b-916b-42f6df89a548'
    ]
    person_names = ['Alex', 'Peter', 'Tom', 'Julia']

    for j in range(1000):
        phone = random.choice(phones)
        person = random.choice(person_names)
        for i in range(10):
            if i % 2 == 0:
                data = {
                    'imei': phone,
                    'personName': person
                }
                response = sender.send_book(data)
                print(response.text)
            else:
                # As last i is 999, the phone should be available.
                data = {
                    'imei': phone
                }
                response = sender.send_return(data)
                print(response.text)

if __name__ == "__main__":
    main()