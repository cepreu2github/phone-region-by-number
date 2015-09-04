describe('phoneRegion controllers', function() {

    describe('MainController', function(){

        it('should create "phones" model with 2 phones', function() {
            var scope = {},
                ctrl = new MainController(scope);

            expect(scope.phones.length).toBe(2);
        });
    });
});